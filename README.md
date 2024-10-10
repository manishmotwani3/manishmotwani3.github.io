# Professor Manish - ANSWER Research Website Documentation

---

## 1. Introduction
- **Project Overview**: This is a research website for Professor Manish, featuring a homepage, research projects, publications, and teaching & students.
- **Website Structure**:
  - [**Homepage**:](#homepage) Social Information, Bio, News
  - [**Research Projects**:](#research-page) Displays Research Group Information and Research Projects
  - [**Publications Page**:](#publication-page) Lists all publications with sorting functionality (Year and Type).
  - [**Teaching and Students**:](#teaching-and-students-page) Displays all current courses and students.

---

## 2. Prerequisites
- **Information**:
  - Each web page has its own directory containing all related files.
  - Pages use JSON files for content updates and dynamic rendering of information.
  - When you change a JSON it should auto update the website but if not try this into the terminal to help with the process: *chmod -R 777 /nfs/stak/users/motwanim/public_html/*

---

## 3. Updating Content

<br></br>
## Homepage 
(Go to homepage.json to update information)
- **Files**:
  - `index.html`: Main HTML file that structures the webpage, with JavaScript for dynamic content rendering.
  - `styles.css`: CSS file for styling the homepage.
  - `homepage.json`: JSON file for professor details, news items, and social links.
  - `homepage folder`:
    - `resource folder`: This contains any images, pdfs, or resources that need to be linked on the homepage.

---
### 1. Left Column (Profile and News)
- **Profile Section**:
  - Displays the professor's image, name, position, department, university, address, and email. Social media icons are created dynamically using Font Awesome.
  - If you would like to change your images or your resume pdfs, put the file into the resource folder in the homepage folder, and then change the URL using this template: "homepage/resources/*file name*"
  - **JSON Format for Social Information:**
  ```json
    {
        "image": "homepage/resources/Motwani.jpg",
        "name": "Manish Motwani",
        "position": "(Assistant Professor)",
        "department": "School of Electrical Engineering and Computer Science",
        "university": "Oregon State University",
        "address": "Room 3043, Kelley Engineering Center 2500 NW Monroe Ave, Corvallis, OR 97331-5501",
        "email": "motwanim[at]oregonstate.edu",
        "socialLinks": [
            {
                "icon": "fas fa-address-card",
                "url": "/~motwanim/homepage/resources/ManishMotwani_CV.pdf",
                "title": "Resume"
            },
            {
                "icon": "fab fa-github",
                "url": "https://github.com/manishmotwani3",
                "title": "GitHub"
            },
            {
                "icon": "fas fa-graduation-cap",
                "url": "https://scholar.google.com/citations?user=OCyPkm0AAAAJ&hl=en",
                "title": "Google Scholar"
            },
            {
                "icon": "fab fa-twitter",
                "url": "https://x.com/manish_motwani?lang=en",
                "title": "Twitter"
            },
            {
                "icon": "fab fa-linkedin",
                "url": "https://www.linkedin.com/in/m-motwani/",
                "title": "LinkedIn"
            }
        ],
    }
  
- **News Section**:
  - Displays a list of recent news items with support for links. News items are ordered based on their position in the JSON file (topmost news in JSON appears first).
  - **JSON Template for News Entry:**
  ```json
  {
      "title": "*insert news title*",
      "url": "/~motwanim/publications/publication_sources/*news resource*",
      "wordToLink": "*word in the news title that you would like to be linked*"
  }

  JSON Example for News Entry:
  {
      "title": "Paper accepted in ICSE'23 (Technical track, Artifact Evaluation track)",
      "url": "/~motwanim/publications/publication_sources/Motwani23icse.pdf",
      "wordToLink": "Paper"
  }

### 2. Right Column (Bio Section)
- **Bio Section**:
  - Displays a short biography fetched from the JSON file. It includes links to relevant pages such as the School of Electrical Engineering and Computer Science (EECS) and Oregon State University.
  - **JSON Format for Bio Section:**
  ```json
  {
      "bio": {
            "description": "I am an assistant professor at the <a href=\"https://engineering.oregonstate.edu/EECS\" target=\"_blank\">School of Electrical Engineering and Computer Science (EECS)</a> at <a href=\"https://oregonstate.edu/\" target=\"_blank\">Oregon State University</a>. My broad research interests include improving software quality by automating software engineering practices. My research involves analyzing large software repositories to learn interesting phenomena and use that knowledge to design novel automation techniques, such as requirements elicitation, testing, and program repair."
  }
---
<br></br>
## Research Page 
(Go to research.json to update information)
- **Files**:
- `research folder`:
  - `research.html`: Main HTML file that structures the webpage, and the JavaScript for rendering the content.
  - `research.css`: External CSS file to style the page.
  - `research.json`: JSON file containing research group information and research projects.
  - `research_images`: Folder containing all the images of the research projects.

---

### 1. Answer Research Group
- **Research Group Info**:
  - Displays the ANSWER Group Logo and information about the research group.
  - **JSON Format for Answer Research Group Description**:
  ```json
  {
      "description": "The Advanced Software Engineering Research (ANSWER) lab (part of the Software Engineering and Human-Computer Interaction group) in the <a href=\"https://engineering.oregonstate.edu/EECS\" target=\"_blank\">School of Electrical Engineering and Computer Science</a> at <a href=\"https://oregonstate.edu/\" target=\"_blank\">Oregon State University</a> focuses on advancing the field of automated software testing, debugging, and repair. Our work spans a range of application domains, including High-Performance Computing (HPC) scientific applications, Reinforcement learning (RL)-based systems, and REST API-based web applications. We aim to develop innovative techniques and tools that can automatically identify and fix defects, ensuring the reliability and robustness of these complex software systems. Our research strives to improve the efficiency and effectiveness of software maintenance and quality assurance processes."
  }
       
### 2. Research Projects
- **Projects**:
  - Displays all research projects. The projects are listed in the order they appear in the `research.json` file, with the first project in the JSON being displayed first on the webpage.
  - To add a new entry, just copy the JSON format entry below, and when adding links move your publication file into the publication_sources folder with this prefix: "/~motwanim/publications/publication_sources/*publication file*"
  - **JSON Template for Research Project Entries**:
  ```json
  {
        "id": "*insert id you want for project*",
        "image": "/~motwanim/research/research_images/*insert project image file*",
        "class": "research-project anchor-target",
        "title": "*insert project title*",
        "description": "insert project description*",
        "links": [
            { "label": "[Paper]", "url": "/~motwanim/publications/publication_sources/*insert publication pdf*" }
        ]
    }

  JSON Example for Research Project Entry:
  {
      {
            "id": "high-quality-apr",
            "image": "/~motwanim/research/research_images/high-quality-apr.jpg",
            "class": "research-project anchor-target",
            "title": "High-Quality Automatic Program Repair",
            "description": "Automatic program repair (APR) has recently gained attention because it proposes to fix software defects with no human intervention. To automatically fix defects, most APR tools use developer-written tests to (a) localize the defect, and (b) generate and validate the automatically produced candidate patches based on the constraints imposed by the tests. While APR tools can produce patches that appear to fix the defect for 11–19% of the defects in real-world software, most of the patches produced are not correct or acceptable to developers because they overfit to the tests used during the repair process. This problem is known as the patch overfitting problem. To address this problem, I propose to equip APR tools with additional constraints derived from natural-language software artifacts such as bug reports and requirements specifications that describe the bug and intended software behavior but are not typically used by APR tools. I hypothesize that patches produced by APR tools while using such additional constraints would be of higher quality. To test this hypothesis, I propose an automated and objective approach to evaluate the quality of patches, and propose two novel methods to improve the fault localization and developer-written test suites using natural-language software artifacts. Finally, I propose to use my patch evaluation methodology to analyze the effect of the improved fault localization and test suites on the quality of patches produced by APR tools for real-world defects.",
            "links": [
                { "label": "[Paper]", "url": "/~motwanim/publications/publication_sources/Motwani21-icseDS.pdf"},
                { "label": "[Dissertation]", "url": "/~motwanim/publications/publication_sources/High-Quality-APR-Dissertation.pdf" },
                { "label": "[Video]", "url": "https://www.youtube.com/watch?v=om7VWRGAMew" }
            ]
        }
  }

---

<br></br>
## Publication Page 
(Go to publications.json to update information)
- **Files**:
- `publication folder`
  - `publication.html`: The main HTML file that structures the webpage, along with the JavaScript for rendering the content.
  - `publication.css`: External CSS file for styling the page.
  - `publication.json`: JSON file containing all the publication data.
  - `publication_sources`: Folder containing all the resources associated with the publications (e.g., PDFs, slides).
  
---

### 1. Publications
- **Publications**:
  - Displays all publications on the page. Publications are grouped by year and can be sorted by type (e.g., journal, conference, workshop).
  - Custom order for publication types:
    - "Refereed Journal Articles"
    - "Refereed Conference Publications"
    - "Refereed Short Publications"
    - "Refereed Book Chapters"
    - "Refereed Workshop Publications"
    - "Refereed Poster and Unconventional Publications"
    - "Patents"
    - "Non-Refereed Publications"

  - **JSON Template for Publication Entry**:
  ```json
  {
        "title": "*insert publication title*",
        "type": "*insert publication type*",
        "year": *insert year*,
        "bold": ["*insert words to be bolded"],
        "italic": ["*insert authors to be italicized"],
        "links": [
            { "text": "[Paper]", "url": "/~motwanim/publications/publication_sources/*insert file to be linked*" }
        ]
    }

    JSON Example for Publication Project Entry:
  {
        "title": "Manish Motwani, Sandhya Sankaranarayanan, René Just, and Yuriy Brun, Do Automated Program Repair Techniques Repair Hard and Important Bugs?, Empirical Software Engineering (EMSE), 2018",
        "type": "Refereed Journal Articles",
        "year": 2018,
        "bold": ["Do Automated Program Repair Techniques Repair Hard and Important Bugs?"],
        "italic": ["Manish Motwani", "Sandhya Sankaranarayanan", "René Just", "Yuriy Brun"],
        "links": [
            { "text": "[Paper]", "url": "/~motwanim/publications/publication_sources/Motwani18icse.pdf" },
            { "text": "[Slides]", "url": "/~motwanim/publications/publication_sources/AutoRepairApplicabilityICSE18.pdf" }
        ]
    },


---

<br></br>
## Teaching and Students Page 
(Go to teaching_and_students.json to update information)
 - **Files**:
 - `teaching_and_students folder`
    - `teaching_and_students.html`: Main HTML file that structures the webpage, and the Javascript of rendering the content.
    - `teaching_and_students.css`: External CSS file to style the page.
    - `teaching_and_students.json`: JSON file containing publications.
- `courses folder`: Folder containing all the canvas courses and relative files with updated links
- `updatelinks.py`: The script to convert your canvas export pages into your courses page for your website, will be saved into the courses folder in the public_html directory.
     
---

- **Courses Section**:
  - Displays the professor's current courses, it will be displayed in the order of the JSON.
  - **JSON Format for Course Entry:**
  ```json
    {
            "term": "Spring 2025",
            "courseNumber": "CS 362: Software Engineering II",
            "url": "/~motwanim/courses/cs569_winter2024/syllabus.html" //copy and paste the path to the course syllabus, will be printed at the end of the updatelinks.py
    }
- **Update Canvas Exports (updatelinks.py)**:
  - You will need to install Beautiful Soup and the Parser: *pip install beautifulsoup4 lxml*
  - Instructions:
      1. Drag the canvas export folder into the parent directory (public_html).
      2. Rename the Canvas Export to whatever you would like (cs563_spring2024, cs569_winter2024)
      3. Run the updatelinks.py script, it will ask to (Enter the path to the Canvas Folder).
      4. Right click the Canvas Export Folder in the public_html directory and press Copy Path and paste it into the terminal.
      5. It will then ask ("Enter the content for header1: "). This will be the Course Number (CS 569, CS 563)
      6. It will then ask ("Enter the content for header2: "). This will be the Course Description (Selected Topics in Software Engineering: Program Analysis and Evaluation)
      7. Once the links are updated, the script will print out the path to the syllabus.html fo the course you just exported. For example: /~motwanim/courses/*exported course number*/syllabus.html
      8. Copy and paste this link into the "URL" entry above in the JSON format for a new Course Entry.
      9. [Tutorial](https://github.com/ANSWER-OSU/ANSWER-ResearchWebsite/blob/main/updatelinks%20script%20tutorial.mkv)

- **Student Section**:
  - Displays the professor's current students, it will be displayed in the order of the JSON.
  - **JSON Format for Student Section:**
  ```json
  {
      "name": "Aakash Kulkarni",
      "standing": "PhD"
  }
---
 




