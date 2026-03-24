import http from 'k6/http';
import { sleep } from 'k6';

const TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3NDEwODc3NCwiZXhwIjoxNzc0NjQ4Nzc0fQ.6dPntq8vmFILkBh02GYHetYSKbZBdPyGTQUNWg3ILeA"

export default function () {
  const payload = JSON.stringify({
    command:"setpos -621.9157 557.1576 9.796875; setang 9.95 45.48 0.00",
    map: "DE_DUST2",
    grenadeType:"weapon_smokegrenade",
    side:"T",
    speed:"295.35275",
    buttons:["LMB"]
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${TOKEN}`,
    },
  };

  http.post(
    'http://192.168.9.50:8080/api/grenades',
    payload,
    params
  );

  sleep(Math.random() * 3 + 1);
}

export const options = {
  stages: [
    { duration: '30s', target: 50 },
    { duration: '30s', target: 100 },
    { duration: '30s', target: 200 },
    { duration: '30s', target: 400 },
    { duration: '30s', target: 800 },
    { duration: '30s', target: 1000 },
    { duration: '30s', target: 1200 },
    { duration: '30s', target: 1400 },
  ],
};