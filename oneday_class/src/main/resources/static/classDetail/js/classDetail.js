// 백엔드에서 넘겨받은 전체 scheduleList를 자바스크립트 객체 배열로 변환
const allSchedules = /*[[${detail.scheduleList}]]*/ []; 

function selectDate(element) {
    // 1. 모든 날짜 버튼의 active 클래스 제거 후 클릭한 버튼에만 active 추가
    document.querySelectorAll('.day-item').forEach(item => item.classList.remove('active'));
    element.classList.add('active');

    // 2. 클릭한 날짜 가져오기 (예: '2026-08-20')
    const selectedDate = element.getAttribute('data-date');

    // 3. 전체 스케줄 중 선택한 날짜와 일치하는 스케줄만 필터링
    const filteredSchedules = allSchedules.filter(s => s.classDate === selectedDate);

    // 4. 시간 슬롯 컨테이너 갱신
    const container = document.getElementById('timeSlotsContainer');
    container.innerHTML = ''; // 기존 카드 삭제

    if (filteredSchedules.length === 0) {
        container.innerHTML = '<div class="no-schedule">해당 날짜에 일정이 없습니다.</div>';
        return;
    }

    // 5. 필터링된 스케줄을 HTML 카드로 만들어 삽입
    filteredSchedules.forEach(schedule => {
        const isSoldOut = schedule.soldOutYn === 'Y';
        const cardHtml = `
            <div class="time-card ${isSoldOut ? 'disabled' : ''}">
                <span class="status-top">${isSoldOut ? '마감됨' : '예약가능'}</span>
                <span class="status-main">${isSoldOut ? '매진' : '잔여 ' + schedule.remainingPeople + '석'}</span>
                <span class="time-range">${schedule.startTime} ~ ${schedule.endTime}</span>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', cardHtml);
    });
}