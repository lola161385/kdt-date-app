// Firebase 설정
const firebaseConfig = {
    apiKey: "AIzaSyC_fS_I_g0jicSPKLqBxF8if2ffnDKrvNM",
    authDomain: "date-app-41a1c.firebaseapp.com",
    projectId: "date-app-41a1c",
    storageBucket: "date-app-41a1c.firebasestorage.app",
    messagingSenderId: "950558236414",
    appId: "1:950558236414:web:f65dad635607345aa8cfaf",
    measurementId: "G-EBTEFW9PDH",
    databaseURL: "https://date-app-41a1c-default-rtdb.asia-southeast1.firebasedatabase.app"
};

// Firebase 초기화
if (!firebase.apps.length) {
    firebase.initializeApp(firebaseConfig);
}

// Firebase 인스턴스 내보내기
const auth = firebase.auth();
const database = firebase.database();

// 이메일 주소를 Firebase Realtime Database 키로 변환
function sanitizeEmail(email) {
    return email.replace(/\./g, '_dot_').replace(/@/g, '_at_');
}

// 채팅방 ID 생성
function generateRoomId(email1, email2) {
    const safe1 = sanitizeEmail(email1);
    const safe2 = sanitizeEmail(email2);
    return safe1.localeCompare(safe2) < 0 ? `${safe1}_${safe2}` : `${safe2}_${safe1}`;
} 