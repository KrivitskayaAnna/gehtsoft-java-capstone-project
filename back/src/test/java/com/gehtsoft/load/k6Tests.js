import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const errorRate = new Rate('errors');
const csrfTokenTime = new Trend('csrf_token_time');
const quizFetchTime = new Trend('quiz_fetch_time');
const quizCheckTime = new Trend('quiz_check_time');
const leaderboardTime = new Trend('leaderboard_time');
const playerStatsTime = new Trend('player_stats_time');

export const options = {
  stages: [
    { duration: '30s', target: 50 },
    { duration: '30s', target: 50 },
    { duration: '30s', target: 100 },
    { duration: '30s', target: 100 }
  ],
  thresholds: {
    errors: ['rate<0.1'],
    http_req_duration: ['p(95)<2000'],
    http_req_failed: ['rate<0.1'],
  },
};

const BASE_URL = 'http://localhost:8080';
const playerNames = ['Ann1', 'Ann2', 'Ann3', 'Ann4', 'Ann5', 'Ann6', 'Ann7', 'Ann8'];
const questionLevels = ['easy', 'medium', 'hard'];

class Session {
  constructor() {
    this.jar = http.cookieJar();
    this.csrfToken = null;
    this.headers = {
      'Content-Type': 'application/json',
      'accept': 'application/json',
    };
  }

  getCSRFToken() {
    const params = {
      headers: this.headers,
      tags: { name: 'get_csrf' },
    };

    const response = http.get(`${BASE_URL}/api/quiz/csrf`, params);

    if (response.status === 200) {
      this.csrfToken = response.body;
      this.headers['X-XSRF-TOKEN'] = this.csrfToken;
    }

    return response;
  }

  get(url, params = {}) {
    const finalParams = {
      headers: this.headers,
      tags: { name: 'get_request' },
      ...params
    };
    return http.get(url, finalParams);
  }

  post(url, body, params = {}) {
    const finalParams = {
      headers: this.headers,
      tags: { name: 'post_request' },
      ...params
    };
    return http.post(url, body, finalParams);
  }
}

export default function () {
  const session = new Session();

  const csrfResponse = session.getCSRFToken();

  const csrfSuccess = check(csrfResponse, {
    'CSRF token status is 200': (r) => r.status === 200,
    'CSRF token is received': (r) => r.body && r.body.length > 0,
  });

  errorRate.add(!csrfSuccess);

  if (csrfResponse && csrfResponse.timings) {
    csrfTokenTime.add(csrfResponse.timings.duration);
  }

  if (!csrfSuccess) {
    console.log('Failed to get CSRF token, skipping iteration');
    sleep(1);
    return;
  }

  const questionsNum = Math.floor(Math.random() * 1000) + 1;
  const questionsLevel = questionLevels[Math.floor(Math.random() * questionLevels.length)];

  const quizResponse = session.get(
    `${BASE_URL}/api/quiz?questionsNum=${questionsNum}&questionsLevel=${questionsLevel}`
  );

  const quizSuccess = check(quizResponse, {
    'Quiz questions status is 200': (r) => r.status === 200,
    'Quiz questions returned': (r) => r.json().length > 0,
  });
  errorRate.add(!quizSuccess);

  if (quizResponse && quizResponse.timings) {
    quizFetchTime.add(quizResponse.timings.duration);
  }

  if (quizSuccess && quizResponse.json().length > 0) {
    const questions = quizResponse.json();
    const playerName = playerNames[Math.floor(Math.random() * playerNames.length)];

    const answers = questions.map(question => ({
      questionId: question.questionId,
      answerIdx: Math.floor(Math.random() * question.answers.length) + 1
    }));

    const checkPayload = JSON.stringify({
      playerName: playerName,
      answers: answers
    });

    const checkResponse = session.post(
      `${BASE_URL}/api/quiz/check`,
      checkPayload
    );

    const checkSuccess = check(checkResponse, {
      'Check answers status is 200': (r) => r.status === 200,
      'Check answers response valid': (r) => {
        if (r.status !== 200) return false;
        try {
          const body = r.json();
          return body.maxScore !== undefined && body.totalScore !== undefined;
        } catch (e) {
          return false;
        }
      },
    });
    errorRate.add(!checkSuccess);

    if (checkResponse && checkResponse.timings) {
      quizCheckTime.add(checkResponse.timings.duration);
    }

    testLeaderboardEndpoints(session, playerName);
  } else {
    const randomPlayer = playerNames[Math.floor(Math.random() * playerNames.length)];
    testLeaderboardEndpoints(session, randomPlayer);
  }

  sleep(1);
}

function testLeaderboardEndpoints(session, playerName) {
  const leadersNum = Math.floor(Math.random() * 1000) + 1;

  const leaderboardResponse = session.get(
    `${BASE_URL}/api/leaderboard?leadersNum=${leadersNum}`
  );

  const leaderboardSuccess = check(leaderboardResponse, {
    'Leaderboard status is 200': (r) => r.status === 200,
    'Leaderboard returned data': (r) => {
      try {
        return r.json().length > 0;
      } catch (e) {
        return false;
      }
    },
  });
  errorRate.add(!leaderboardSuccess);

  if (leaderboardResponse && leaderboardResponse.timings) {
    leaderboardTime.add(leaderboardResponse.timings.duration);
  }

  const playerStatsResponse = session.get(
    `${BASE_URL}/api/leaderboard/${encodeURIComponent(playerName)}`
  );

  const playerStatsSuccess = check(playerStatsResponse, {
    'Player stats status is 200': (r) => r.status === 200,
    'Player stats returned': (r) => {
      if (r.status !== 200) return false;
      try {
        const body = r.json();
        return body.playerName && body.totalScore !== undefined;
      } catch (e) {
        return false;
      }
    },
  });
  errorRate.add(!playerStatsSuccess);

  if (playerStatsResponse && playerStatsResponse.timings) {
    playerStatsTime.add(playerStatsResponse.timings.duration);
  }
}