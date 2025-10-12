## Java capstone project

### Description

An application with backend and frontend components
that displays questions with answer choices to the user.
After completing the quiz, the result is shown and the overall quiz leaderboard is updated.

### Project plan

1. Implement java rest api application with endpoints:

- GET /api/quiz?questionsNum={5}&questionsLevel={easy} <br>
  Returns questionsNum random questions of questionsLevel difficulty with answer variants from external REST API
  containing quiz questions.
  Saves question IDs with the correct answer IDs and score points (1 score point for an easy question, 2 points for
  medium,
  and 3 for hard) into the database.

- POST /api/quiz/check <br>
  Receives the player's answers, checks them for correctness, and returns the player's overall quiz score.
  Saves it into the database.

- GET /api/leaderboard?leadersNum={5} <br>
  Returns the current top 5 players leaderboard - player name, total score, and their place on the leaderboard.

- GET /api/leaderboard/{playerName} <br>
  Returns the current total score and place on the leaderboard for specified player.

2. Implement javascript frontend with pages (see front/prototype/capstone-ui-prototype.png):

- quiz starter page <br>
  Contains a form that a player should fill in to start the game:
  playerName (English alphabet and numbers), questionsNum (1 to 10), questionsLevel (easy/medium/hard)

- quiz page <br>
  The page the player is redirected to after submitting the starter form.
  There are questions with answer options; the player can choose one of the variants and then submit the quiz.
  After submitting, the answers are checked and the total player score is shown.
  There is also a button "go to the leaderboard".

- leaderboard page <br>
  The final page the player is taken to after clicking "go to the leaderboard" showing the top 5 players ordered by
  their
  total score in all games.
  There is also a button "start new quiz" that redirects the player to the starter page.

### Stack

#### backend

- java spring, jdbc template
- postgres
- rest api

#### frontend

- javascript
- html
- css

#### external resources

Quiz questions are taken from Open Trivia Database https://opentdb.com <br>
Sample curl: https://opentdb.com/api.php?amount=3&difficulty=easy

#### backend local curls

1) GET /api/quiz
curl -X 'GET' \
   'http://localhost:8080/api/quiz?questionsLevel=easy&questionsNum=2' \
   -H 'accept: application/json'

2) POST /api/quiz/check
curl -X 'POST' \
   'http://localhost:8080/api/quiz/check' \
   -H 'accept: application/json' \
   -H 'Content-Type: application/json' \
   -d '{
   "playerName": "Ann",
   "answers": [
   {
   "questionId": 29,
   "answerIdx": 3
   },
   {
   "questionId": 30,
   "answerIdx": 2
   }
   ]
   }'

3) GET /api/leaderboard
curl -X 'GET' \
   'http://localhost:8080/api/leaderboard?leadersNum=3' \
   -H 'accept: application/json'

4) GET /api/leaderboard/{playerName}
 curl -X 'GET' \
   'http://localhost:8080/api/leaderboard/Ann' \
   -H 'accept: application/json'
