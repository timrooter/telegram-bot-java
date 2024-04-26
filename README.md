# telegram-bot-java
**Telegram Chat Bot using Java Spring Boot**

This project implements a Telegram chat bot using Java Spring Boot, allowing users to anonymously submit complaints to a manager's Telegram account. The bot provides a simple interface for users to initiate a complaint, specify details, and submit it. Here's a brief overview:

**Features:**

**Anonymous Complaint Submission:** Users can anonymously submit complaints about the service or personnel by interacting with the bot.
**Initiating Complaint:** Users can start the complaint process by sending the "/start" command. They are then guided through the complaint submission process.
**Interactive Interface:** The bot provides an interactive interface with inline keyboards, making it easy for users to navigate and submit complaints.
**Real-time Communication:** The bot utilizes Telegram's Long Polling mechanism to handle real-time communication and updates from users.
**Error Handling:** The bot includes error handling mechanisms to ensure smooth operation and provides appropriate feedback to users in case of errors.

## Components:

**Telegram Bot Class:** Implements the TelegramLongPollingBot interface, handling incoming updates, processing messages, and managing complaint submission flow.
**ComplainingMessage Class:** Represents a complaint message with details such as position, time, and description.
**Bot Configuration:** Configures bot settings such as name and token through the BotConfig class.
**Dependencies:** Utilizes Spring Boot and TelegramBots Java Library for building the bot application.

How to Use:

Start the Bot: 
Before starting you must know your **digital ID of managers's telegram account** (you can find it in @getmyid_bot), also your **Bot Name and Bot token** and enter in **application.properties**

**Initiate Complaint:** Users can initiate a complaint by clicking the "Create Complaint" button. They will then be prompted to provide details about the complaint.
**Provide Details:** Users are guided through the process of specifying the personnel involved, time of incident, and a description of the issue.
Submit Complaint: After providing all necessary details, users can submit the complaint, which will be anonymously forwarded to the manager's Telegram account.
**Setup and Deployment:**

1) Clone the repository and import the project into your preferred Java IDE.
2) Configure the bot settings such as name and token in the BotConfig class.
3) Build and deploy the application to a server or cloud platform compatible with Java Spring Boot applications.
4) Obtain the manager's Telegram account ID and configure it in the bot settings to forward complaints.


**Contributing:**
Contributions to the project are welcome! Feel free to submit bug fixes, enhancements, or new features via pull requests.
![image](https://github.com/timrooter/telegram-bot-java/assets/146642629/7b44685c-dbfd-45fc-acaa-0fcb6d241106)

<img width="1034" alt="image" src="https://github.com/timrooter/telegram-bot-java/assets/146642629/2db739c1-5f16-401a-a2d6-89a2c14d9c87">
