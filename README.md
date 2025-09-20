## Java capstone project

### Description

An application with backend and frontend components
that displays questions with answer choices to the user.
After completing the quiz, the result is shown and the overall quiz leaderboard is updated.

### Project plan

1. Implement java rest api application with endpoints:

- GET /api/quiz?questionsNum={5}&questionsLevel={easy}
  Returns questionsNum random questions of questionsLevel difficulty with answer variants from external rest api
  containing quiz questions.
  Saves questionIds with the correct answerIds and score points (1 score point for an easy question, 2 points for medium
  and 3 for hard) into database.

- POST /api/quiz/check/{playerName}
  Receives the player answers, checks them for correctness and gives back player's overall quiz score.
  Saves it into database.

- GET /api/leaderboard
  Returns the current top-5 players leaderboard - a playerName, a total score and their place on the leaderboard.

2. Implement javascript frontend with pages:

- quiz starter page
  Contains a form that a player should fill in to start the game:
  playerName (english alphabet and numbers), questionsNum (1 to 10), questionsLevel (easy/medium/hard)

- quiz page
  The page the player is redirected to after submitting the starter form.
  There are questions with answer options, the player can choose on of variants and than submit the quiz.
  After submitting, the answers are checked and the total player score is shown.
  There is also a button 'go to the leaderboard'.

- leaderboard page
  The final page the player is taken to after clicking 'go to the leaderboard' showing top-5 players ordered by their
  total score in all games.
  There is also a button 'start new quiz', that redirects player to starter page.

### Stack

#### backend

- java spring, jdbc
- postgres
- rest api

#### frontend

- javascript
- html
- css

#### external resources

Quiz questions are taken from Open Trivia Database https://opentdb.com
sample curl: https://opentdb.com/api.php?amount=3&difficulty=easy

