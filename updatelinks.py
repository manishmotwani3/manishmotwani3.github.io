import os
import shutil
import re
from bs4 import BeautifulSoup
from urllib.parse import unquote

def extract_files_and_folders(canvas_folder, base_output_folder):
    course_name = os.path.basename(canvas_folder.rstrip('/'))
    output_folder = os.path.join(base_output_folder, course_name)
    os.makedirs(output_folder, exist_ok=True)

    for root, dirs, files in os.walk(canvas_folder):
        for file in files:
            if file.endswith('.html'):
                html_file_path = os.path.join(root, file)
                shutil.copy(html_file_path, output_folder)

        for dir_name in dirs:
            if dir_name == "web_resources":
                src_folder_path = os.path.join(root, dir_name)
                dst_folder_path = os.path.join(output_folder, dir_name)
                shutil.copytree(src_folder_path, dst_folder_path, dirs_exist_ok=True)
                break

    syllabus_path = os.path.join(output_folder, "syllabus.html")
    if os.path.exists(syllabus_path):
        h1_content = input("Enter the content for <h1>: ").strip()
        h2_content = input("Enter the content for <h2>: ").strip()
        generate_navbar(syllabus_path, h1_content, h2_content)

    update_links_and_images_in_html_files(output_folder)

    return syllabus_path

def generate_navbar(html_path, h1_content, h2_content):
    with open(html_path, 'r', encoding='utf-8') as file:
        html_content = file.read()

    soup = BeautifulSoup(html_content, 'html.parser')

    title_div = soup.new_tag('div', attrs={'class': 'title'})
    
    h1_tag = soup.new_tag('h1')
    h1_tag.string = h1_content
    title_div.append(h1_tag)
    
    h2_tag = soup.new_tag('h2')
    h2_tag.string = h2_content
    title_div.append(h2_tag)
    
    soup.body.insert(0, title_div)

    h4_tags = soup.find_all('h4')
    
    nav_bar = soup.new_tag('nav', attrs={'class': 'navbar'})

    for index, h4_tag in enumerate(h4_tags, start=1):
        h4_text = h4_tag.get_text(strip=True, separator='\n')
        
        if h4_text:
            nav_text = h4_text.split('\n')[1].strip() if '\n' in h4_text else h4_text.strip()
            
            p_tag = soup.new_tag('p')
            a_tag = soup.new_tag('a', href=f'#{index}')
            a_tag.string = nav_text.rstrip(':')
            p_tag.append(a_tag)

            if index < len(h4_tags):
                p_tag.append(" |")

            nav_bar.append(p_tag)
            h4_tag['id'] = str(index)

    title_div.insert_after(nav_bar)

    style_tag = soup.new_tag('style')
    style_tag.append("""
    .title {
        text-align: center;
    }

    .title h1 {
        margin-bottom: 0px;
    }

    .title h2 {
        margin-top: 0px;
    }
    
    .navbar {
        margin-top: 10px;
        margin-bottom: 20px;
        background-color: #f8f9fa;
        padding: 10px;
        border-radius: 5px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
    
    .navbar p {
        display: inline;
        margin-right: 5px;
        text-align: justify;
    }

    .navbar a {
        text-decoration: none;
    }

    .navbar a:hover {
        text-decoration: underline;
        color:  #2e60b6;
    }
    """)
    soup.head.append(style_tag)

    with open(html_path, 'w', encoding='utf-8') as file:
        file.write(str(soup))

    print(f'Generated navbar for {html_path}')

def normalize_string(s):
    s = s.replace('#', 'number-')
    return re.sub(r'[^a-zA-Z0-9]', '-', s.lower()).replace('--', '-').strip('-')

def update_ims_cc_links(soup, prefix, output_folder):
    ims_cc_links = soup.find_all('a', href=True, title=True)
    for link in ims_cc_links:
        href = link['href']
        title = link.get('title', '').strip()

        href = href.split('?')[0]

        if '$IMS-CC-FILEBASE$' in href and title:
            found = False
            
            decoded_href = unquote(href)
            file_name = os.path.basename(decoded_href)

            file_name_normalized = normalize_string(file_name)

            for root, _, files in os.walk(output_folder):
                for file in files:
                    file_normalized = normalize_string(file)
                    if file_name_normalized == file_normalized:
                        relative_path = os.path.relpath(os.path.join(root, file), output_folder)
                        new_path = f'{prefix}/{relative_path}'
                        link['href'] = new_path
                        found = True
                        break
                if found:
                    break

            if not found:
                print(f'IMS-CC file "{title}" not found for href: "{href}" in {output_folder}')

def update_canvas_object_links(soup, prefix, output_folder):
    canvas_links = soup.find_all('a', href=True, title=True)
    for link in canvas_links:
        href = link['href']
        title = link.get('title', '').strip()

        if '$CANVAS_OBJECT_REFERENCE$' in href and title:
            found = False

            title_normalized = normalize_string(title)

            for root, _, files in os.walk(output_folder):
                for file in files:
                    file_name, file_extension = os.path.splitext(file)
                    file_normalized = normalize_string(file_name)

                    if title_normalized == file_normalized:
                        relative_path = os.path.relpath(os.path.join(root, file), output_folder)
                        new_path = f'{prefix}/{relative_path}'
                        link['href'] = new_path
                        found = True
                        break
                if found:
                    break

            if not found:
                print(f'Canvas object file "{title}" not found for href: "{href}" in {output_folder}')

def update_image_src_attributes(soup, export_dir):
    prefix = '/~motwanim/courses'
    img_tags = soup.find_all('img', src=re.compile(r'^\$IMS-CC-FILEBASE\$/Uploaded%20Media/'))
    for img in img_tags:
        src = img['src']
        filename = unquote(src.split('/')[-1])

        found = False
        for root, _, files in os.walk(export_dir):
            if filename in files:
                relative_path = os.path.relpath(os.path.join(root, filename), export_dir)
                new_src = f'{prefix}/{relative_path}'
                img['src'] = new_src
                found = True
                break

        if not found:
            print(f'Image file {filename} not found in {export_dir}')

def update_links_and_images(html_file_path, output_folder):
    with open(html_file_path, 'r', encoding='utf-8') as file:
        html_content = file.read()

    soup = BeautifulSoup(html_content, 'html.parser')

    base_folder = os.path.basename(output_folder)
    prefix = f'/~motwanim/courses/{base_folder}'

    update_ims_cc_links(soup, prefix, output_folder)
    update_canvas_object_links(soup, prefix, output_folder)
    update_image_src_attributes(soup, output_folder)

    with open(html_file_path, 'w', encoding='utf-8') as file:
        file.write(str(soup))

def update_links_and_images_in_html_files(output_folder):
    for root, dirs, files in os.walk(output_folder):
        for file in files:
            if file.endswith('.html'):
                html_file_path = os.path.join(root, file)
                update_links_and_images(html_file_path, output_folder)

if __name__ == '__main__':
    canvas_folder = input("Enter the path to the Canvas folder: ").strip()
    base_output_folder = "/nfs/stak/users/motwanim/public_html/courses"
    syllabus_path = extract_files_and_folders(canvas_folder, base_output_folder)
    if syllabus_path:
        relative_path = os.path.relpath(syllabus_path, base_output_folder)
        new_path = f'/~motwanim/courses/{relative_path}'
        print(f'Syllabus file path: {new_path}')
