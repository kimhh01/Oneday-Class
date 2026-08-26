/**
 * 맵 자바스크립트 (동그라미 커스텀 오버레이 마커 + 지도 영역 & 카테고리 필터 연동)
 */
document.addEventListener("DOMContentLoaded", function() {

    // -------------------------------------------------------------
    // 1. 카카오 지도 생성 및 전역 변수 선언
    // -------------------------------------------------------------
    const mapContainer = document.getElementById('map');
    let allClassData = typeof classListData !== 'undefined' ? classListData : [];
    let allCategories = typeof categoryListData !== 'undefined' ? categoryListData : [];
    console.log("allClassData 개수:", allClassData.length);

    let markers = []; // 생성된 커스텀 오버레이 객체 저장 배열

    // 필터용 전역 변수
    let selectedCategoryCode = "";
    let selectedCategoryName = "카테고리 전체";
    let selectedDate = ""; // 선택된 날짜 (예: "2026-08-17")
    let selectedTime = ""; // 선택된 시간 (예: "15:00")

    let defaultLat = 37.5381;
    let defaultLng = 127.11609;

    if (allClassData.length > 0 && allClassData[0].lat && allClassData[0].lng) {
        defaultLat = allClassData[0].lat;
        defaultLng = allClassData[0].lng;
    }

    const mapOption = {
        center: new kakao.maps.LatLng(defaultLat, defaultLng),
        level: 4
    };

    const map = new kakao.maps.Map(mapContainer, mapOption);
    const geocoder = new kakao.maps.services.Geocoder();
    const ps = new kakao.maps.services.Places();

    // -------------------------------------------------------------
    // 2. 카테고리별 물방울 마커 스타일 & 생성 함수
    // -------------------------------------------------------------
    const CATEGORY_STYLE = {
        '1': { icon: 'fa-solid fa-scissors', name: '공예' },
        '2': { icon: 'fa-solid fa-utensils', name: '요리' },
        '3': { icon: 'fa-solid fa-person-running', name: '액티비티' },
        '4': { icon: 'fa-solid fa-palette', name: '미술' },
        '5': { icon: 'fa-solid fa-sparkles', name: '뷰티' },
        '6': { icon: 'fa-solid fa-music', name: '음악' },
        'DEFAULT': { icon: '/map/images/cook-marker.png', name: '클래스' }
    };

	function createCustomMarker(item) {
	    let parentCategoryCode = 'DEFAULT';
	    if (item.categoryCode !== undefined && item.categoryCode !== null) {
	        parentCategoryCode = String(item.categoryCode).charAt(0);
	    }

	    const style = CATEGORY_STYLE[parentCategoryCode] || CATEGORY_STYLE['DEFAULT'];

	    const contentHtml = `
	        <div class="custom-pin-wrapper" title="${item.name || ''}" style="
	            position: relative;
	            width: 28px;
	            height: 28px;
	            background-color: #1B5E20;
	            border-radius: 50% 50% 50% 0;
	            transform: rotate(-45deg);
	            box-shadow: -2px 3px 6px rgba(0, 0, 0, 0.25);
	            display: flex;
	            align-items: center;
	            justify-content: center;
	            cursor: pointer;
	            transition: transform 0.2s ease;
	        ">
	            <i class="${style.icon}" style="
	                transform: rotate(45deg);
	                font-size: 13px;
	                color: #ffffff;
	                display: flex;
	                align-items: center;
	                justify-content: center;
	            "></i>
	        </div>
	    `;

	    const container = document.createElement('div');
	    container.innerHTML = contentHtml;
	    const markerElement = container.firstElementChild;

	    markerElement.addEventListener('mouseenter', function() {
	        this.style.transform = 'rotate(-45deg) scale(1.15)';
	    });
	    markerElement.addEventListener('mouseleave', function() {
	        this.style.transform = 'rotate(-45deg) scale(1.0)';
	    });

	    markerElement.addEventListener('click', function() {
	        map.panTo(new kakao.maps.LatLng(item.lat, item.lng));
	    });

	    return new kakao.maps.CustomOverlay({
	        position: new kakao.maps.LatLng(item.lat, item.lng),
	        content: markerElement,
	        xAnchor: 0.5,
	        yAnchor: 1.0
	    });
	}

    // -------------------------------------------------------------
    // 3. 지도의 현재 범위(Bounds) + 필터 조건 동시 필터링
    // -------------------------------------------------------------
    // -------------------------------------------------------------
    // 3. 지도의 현재 범위(Bounds) + 필터 조건 동시 필터링
    // -------------------------------------------------------------
    function filterClassesByMapBounds() {
        const bounds = map.getBounds();
        const swLatLng = bounds.getSouthWest();
        const neLatLng = bounds.getNorthEast();

        const minLat = swLatLng.getLat();
        const maxLat = neLatLng.getLat();
        const minLng = swLatLng.getLng();
        const maxLng = neLatLng.getLng();

        const filteredClasses = allClassData.filter(item => {
            if (!item.lat || !item.lng) return false;

            // 1. 지도 화면 영역(Bounds) 내 존재 여부
            const isInMapBounds = (item.lat >= minLat && item.lat <= maxLat && item.lng >= minLng && item.lng <= maxLng);

            // 2. 지역 (동/구) 필터링
            const targetAddress = item.address || item.oldAddress || '';
            const isMatchingRegion = !selectedDong || targetAddress.includes(selectedDong);

            // 3. 카테고리 필터링
            const isMatchingCategory = !selectedCategoryCode ||
                (String(item.categoryCode) === String(selectedCategoryCode) ||
                    String(item.parentCategoryCode) === String(selectedCategoryCode));

            // ---------------------------------------------------------
            // 4. 날짜 및 시간 필터링 (스케줄 리스트 1:1 정밀 검사)
            // ---------------------------------------------------------
            const dateList = item.classDate ? String(item.classDate).split(',') : [];
            const timeList = item.startTime ? String(item.startTime).split(',') : [];

            let isMatchingSchedule = true;

            if (selectedDate || selectedTime) {
                // 날짜와 시간 조건이 하나라도 걸려있으면 스케줄 목록 중 만족하는 스케줄이 1개라도 있는지 확인
                isMatchingSchedule = dateList.some((dStr, idx) => {
                    const trimmedDate = dStr.trim();
                    const trimmedTime = timeList[idx] ? timeList[idx].trim() : '';

                    const datePass = !selectedDate || trimmedDate.includes(selectedDate);
                    const timePass = !selectedTime || trimmedTime.includes(selectedTime);

                    return datePass && timePass;
                });
            }

            return isInMapBounds && isMatchingRegion && isMatchingCategory && isMatchingSchedule;
        });

        renderMarkersAndSidebar(filteredClasses);
    }


    function renderMarkersAndSidebar(classList) {
        markers.forEach(overlay => overlay.setMap(null));
        markers = [];

        const cardContainer = document.getElementById('card-list-container');
        const countNumSpan = document.getElementById('class-count-num');

        if (countNumSpan) countNumSpan.innerText = classList ? classList.length : 0;

        if (!classList || classList.length === 0) {
            if (cardContainer) {
                cardContainer.innerHTML = '<div class="no-data-msg">현재 지도 화면 내에 등록된 클래스가 없습니다.</div>';
            }
            return;
        }

        // 좌측 사이드바 카드 HTML 생성
        if (cardContainer) {
            let cardHtml = '';
            classList.forEach(item => {
                const imgPath = (item.imageList && item.imageList.length > 0) ? item.imageList[0].image : '/images/default.jpg';
                const formattedPrice = item.price ? Number(item.price).toLocaleString() + '원' : '0원';
                const targetId = item.classCode || item.classcode || item.classNo || item.id;

                cardHtml += `
                    <div class="class-card" data-id="${targetId}" data-lat="${item.lat}" data-lng="${item.lng}" data-title="${item.name}">
                        <div class="img-wrapper">
                            <img src="${imgPath}" class="class-img" alt="클래스 이미지">
                            <button class="wish-btn" aria-label="관심목록 추가">♡</button>
                        </div>
                        <div class="card-info">
                            <div>
                                <h3 class="class-title">${item.name}</h3>
                                <span class="category-badge">${item.categoryName || '클래스'}</span>
                                <p class="class-address">${item.address || ''}</p>
                            </div>
                            <div class="price-row">
                                <span class="class-price">${formattedPrice}</span>
                                <span class="class-distance">📍 지도 내 위치</span>
                            </div>
                        </div>
                    </div>
                `;
            });
            cardContainer.innerHTML = cardHtml;
        }

        // 마커 생성
        classList.forEach(item => {
            if (item.lat && item.lng) {
                const customOverlay = createCustomMarker(item);
                customOverlay.setMap(map);
                markers.push(customOverlay);
            }
        });
    }

    // 지도 움직임(이동, 확대, 축소)이 끝날 때 재필터링
    kakao.maps.event.addListener(map, 'idle', function() {
        filterClassesByMapBounds();
    });

    // -------------------------------------------------------------
    // 4. 지역 선택 모달 이벤트
    // -------------------------------------------------------------
    const btnRegionToggle = document.getElementById('btn-region-toggle');
    const regionModal = document.getElementById('region-modal');
    const btnRegionCancel = document.getElementById('btn-region-cancel');
    const btnRegionSearch = document.getElementById('btn-region-search');
    const listSido = document.getElementById('list-sido');
    const listSigungu = document.getElementById('list-sigungu');
    const listDong = document.getElementById('list-dong');

    let selectedSido = "";
    let selectedSigungu = "";
    let selectedDong = "";

    btnRegionToggle?.addEventListener('click', (e) => {
        e.stopPropagation();
        categoryModal?.classList.add('hidden');
        dateModal?.classList.add('hidden');
        timeModal?.classList.add('hidden');
        regionModal?.classList.toggle('hidden');
    });

    btnRegionCancel?.addEventListener('click', (e) => {
        e.stopPropagation();
        regionModal?.classList.add('hidden');
    });

    listSido?.addEventListener('click', function(e) {
        const target = e.target.closest('li');
        if (!target) return;

        listSido.querySelectorAll('li').forEach(el => el.classList.remove('active'));
        target.classList.add('active');

        selectedSido = target.getAttribute('data-sido') || "";
        selectedSigungu = "";
        selectedDong = "";

        // 하위 구/동 선택 초기화
        listSigungu.innerHTML = '<li class="active" data-sigungu="">선택안함</li>';
        listDong.innerHTML = '<li class="active" data-dong="">선택안함</li>';

        // 시/도가 '전체'가 아니고 특정 지역일 때만 카카오 키워드 검색 실행
        if (selectedSido) {
            ps.keywordSearch(`${selectedSido}`, function(data, status) {
                listSigungu.innerHTML = '<li class="active" data-sigungu="">선택안함</li>';
                if (status === kakao.maps.services.Status.OK) {
                    const sigunguSet = new Set();
                    data.forEach(place => {
                        const parts = place.address_name.split(' ');
                        if (parts.length > 1 && (parts[1].endsWith('구') || parts[1].endsWith('군') || parts[1].endsWith('시'))) {
                            sigunguSet.add(parts[1]);
                        }
                    });
                    sigunguSet.forEach(sigungu => {
                        const li = document.createElement('li');
                        li.setAttribute('data-sigungu', sigungu);
                        li.textContent = sigungu;
                        listSigungu.appendChild(li);
                    });
                }
            });
        }
    });

    listSigungu?.addEventListener('click', function(e) {
        const target = e.target.closest('li');
        if (!target) return;

        listSigungu.querySelectorAll('li').forEach(el => el.classList.remove('active'));
        target.classList.add('active');

        selectedSigungu = target.getAttribute('data-sigungu') || "";
        if (selectedSigungu === '선택안함') selectedSigungu = '';

        if (!selectedSigungu) {
            listDong.innerHTML = '<li class="active" data-dong="선택안함">선택안함</li>';
            return;
        }

        ps.keywordSearch(`${selectedSido} ${selectedSigungu}`, function(data, status) {
            listDong.innerHTML = '<li class="active" data-dong="선택안함">선택안함</li>';
            if (status === kakao.maps.services.Status.OK) {
                const dongSet = new Set();
                data.forEach(place => {
                    const parts = place.address_name.split(' ');
                    if (parts.length > 2 && (parts[2].endsWith('동') || parts[2].endsWith('읍') || parts[2].endsWith('면'))) {
                        dongSet.add(parts[2]);
                    }
                });
                dongSet.forEach(dong => {
                    const li = document.createElement('li');
                    li.setAttribute('data-dong', dong);
                    li.textContent = dong;
                    listDong.appendChild(li);
                });
            }
        });
    });

    listDong?.addEventListener('click', function(e) {
        const target = e.target.closest('li');
        if (!target) return;

        listDong.querySelectorAll('li').forEach(el => el.classList.remove('active'));
        target.classList.add('active');

        selectedDong = target.getAttribute('data-dong') || "";
        if (selectedDong === '선택안함') selectedDong = '';
    });

    // btnRegionSearch 클릭 이벤트 내부 수정
    btnRegionSearch?.addEventListener('click', function(e) {
        e.stopPropagation();

        // 시/도가 빈값(전체)이거나 '선택안함'인 경우 -> '지역 전체'로 리셋
        if (!selectedSido || selectedSido === '선택안함') {
            selectedSido = "";
            selectedSigungu = "";
            selectedDong = "";

            const textSpan = btnRegionToggle.querySelector('.select-text');
            if (textSpan) textSpan.innerText = "지역 전체";

            regionModal?.classList.add('hidden');
            filterClassesByMapBounds(); // 지역 조건 없이 현재 지도 범위 기준 조회
            return;
        }

        // 특정 지역(서울, 경기 등)을 선택한 경우 Geocoder 이동
        const fullAddress = `${selectedSido} ${selectedSigungu} ${selectedDong}`.trim();

        geocoder.addressSearch(fullAddress, function(result, status) {
            if (status === kakao.maps.services.Status.OK) {
                const coords = new kakao.maps.LatLng(result[0].y, result[0].x);
                map.setCenter(coords);
                map.setLevel(4);
                regionModal?.classList.add('hidden');

                const textSpan = btnRegionToggle.querySelector('.select-text');
                if (textSpan) {
                    textSpan.innerText = `${selectedSigungu || selectedSido} ${selectedDong}`.trim();
                }

                setTimeout(() => {
                    filterClassesByMapBounds();
                }, 100);
            } else {
                alert("해당 지역의 위치를 찾을 수 없습니다.");
            }
        });
    });

    // -------------------------------------------------------------
    // 5. 날짜 및 시간 선택 관련
    // -------------------------------------------------------------
    const btnDateToggle = document.getElementById('btn-date-toggle');
    const dateModal = document.getElementById('date-modal');
    const btnDateCancel = document.getElementById('btn-date-cancel');
    const btnDateSearch = document.getElementById('btn-date-search');
    const listDate = document.getElementById('list-date');

    const btnTimeToggle = document.getElementById('btn-time-toggle');
    const timeModal = document.getElementById('time-modal');
    const btnTimeCancel = document.getElementById('btn-time-cancel');
    const btnTimeSearch = document.getElementById('btn-time-search');
    const listTime = document.getElementById('list-time');

    function initDateList() {
        if (!listDate) return;
        listDate.innerHTML = '<li class="active" data-date="">전체 날짜</li>';

        const weekDays = ['일', '월', '화', '수', '목', '금', '토'];
        const today = new Date();

        for (let i = 0;i < 7;i++) {
            const d = new Date();
            d.setDate(today.getDate() + i);

            const year = d.getFullYear();
            const month = String(d.getMonth() + 1).padStart(2, '0');
            const date = String(d.getDate()).padStart(2, '0');
            const dayName = weekDays[d.getDay()];

            const formattedValue = `${year}-${month}-${date}`;
            const displayText = i === 0
                ? `오늘 (${month}.${date} ${dayName})`
                : `${month}.${date} (${dayName})`;

            const li = document.createElement('li');
            li.setAttribute('data-date', formattedValue);
            li.textContent = displayText;
            listDate.appendChild(li);
        }
    }

    function initTimeList() {
        if (!listTime) return;
        listTime.innerHTML = '<li class="active" data-time="">전체 시간</li>';

        for (let hour = 0;hour < 24;hour++) {
            const formattedHour = String(hour).padStart(2, '0') + ':00';
            const li = document.createElement('li');
            li.setAttribute('data-time', formattedHour);
            li.textContent = formattedHour;
            listTime.appendChild(li);
        }
    }

    initDateList();
    initTimeList();

    btnDateToggle?.addEventListener('click', (e) => {
        e.stopPropagation();
        regionModal?.classList.add('hidden');
        categoryModal?.classList.add('hidden');
        timeModal?.classList.add('hidden');
        dateModal?.classList.toggle('hidden');
    });

    btnDateCancel?.addEventListener('click', (e) => {
        e.stopPropagation();
        dateModal?.classList.add('hidden');
    });

    listDate?.addEventListener('click', function(e) {
        const target = e.target.closest('li');
        if (!target) return;

        listDate.querySelectorAll('li').forEach(el => el.classList.remove('active'));
        target.classList.add('active');

        selectedDate = target.getAttribute('data-date') || "";
    });

    btnDateSearch?.addEventListener('click', function(e) {
        e.stopPropagation();
        dateModal?.classList.add('hidden');

        const textSpan = btnDateToggle.querySelector('.select-text');
        if (textSpan) {
            const activeLi = listDate.querySelector('li.active');
            textSpan.innerText = selectedDate ? activeLi.innerText : "날짜 선택";
        }

        filterClassesByMapBounds();
    });

    btnTimeToggle?.addEventListener('click', (e) => {
        e.stopPropagation();
        regionModal?.classList.add('hidden');
        categoryModal?.classList.add('hidden');
        dateModal?.classList.add('hidden');
        timeModal?.classList.toggle('hidden');
    });

    btnTimeCancel?.addEventListener('click', (e) => {
        e.stopPropagation();
        timeModal?.classList.add('hidden');
    });

    listTime?.addEventListener('click', function(e) {
        const target = e.target.closest('li');
        if (!target) return;

        listTime.querySelectorAll('li').forEach(el => el.classList.remove('active'));
        target.classList.add('active');

        selectedTime = target.getAttribute('data-time') || "";
    });

    btnTimeSearch?.addEventListener('click', function(e) {
        e.stopPropagation();
        timeModal?.classList.add('hidden');

        const textSpan = btnTimeToggle.querySelector('.select-text');
        if (textSpan) {
            textSpan.innerText = selectedTime ? selectedTime : "시간 선택";
        }

        filterClassesByMapBounds();
    });

    // -------------------------------------------------------------
    // 6. 내 위치 버튼 이벤트
    // -------------------------------------------------------------
    const btnMyLocation = document.getElementById('btn-my-location');
    btnMyLocation?.addEventListener('click', function() {
        if (!confirm("현재 위치를 기반으로 가까운 클래스를 찾으시겠습니까?\n(위치 정보 제공 동의 필요)")) {
            return;
        }

        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                function(position) {
                    const locPosition = new kakao.maps.LatLng(position.coords.latitude, position.coords.longitude);
                    map.setCenter(locPosition);
                    map.setLevel(4);
                },
                function(error) {
                    switch (error.code) {
                        case error.PERMISSION_DENIED:
                            alert("위치 권한 요청이 거부되었습니다. 브라우저 설정에서 위치 권한을 허용해 주세요.");
                            break;
                        case error.POSITION_UNAVAILABLE:
                            alert("현재 위치 정보를 가져올 수 없습니다.");
                            break;
                        case error.TIMEOUT:
                            alert("위치 정보 요청 시간이 초과되었습니다.");
                            break;
                        default:
                            alert("위치 정보를 가져오는 중 오류가 발생했습니다.");
                            break;
                    }
                },
                {
                    enableHighAccuracy: true,
                    timeout: 5000
                }
            );
        } else {
            alert("현재 사용 중인 브라우저에서는 위치 서비스를 지원하지 않습니다.");
        }
    });

    // -------------------------------------------------------------
    // 7. 카테고리 선택 모달 이벤트
    // -------------------------------------------------------------
    const btnCategoryToggle = document.getElementById('btn-category-toggle');
    const categoryModal = document.getElementById('category-modal');
    const btnCategoryCancel = document.getElementById('btn-category-cancel');
    const btnCategorySearch = document.getElementById('btn-category-search');

    const listParentCategory = document.getElementById('list-parent-category');
    const listChildCategory = document.getElementById('list-child-category');

    btnCategoryToggle?.addEventListener('click', (e) => {
        e.stopPropagation();
        regionModal?.classList.add('hidden');
        dateModal?.classList.add('hidden');
        timeModal?.classList.add('hidden');
        categoryModal?.classList.toggle('hidden');
    });

    btnCategoryCancel?.addEventListener('click', (e) => {
        e.stopPropagation();
        categoryModal?.classList.add('hidden');
    });

    listParentCategory?.addEventListener('click', function(e) {
        const target = e.target.closest('li');
        if (!target) return;

        listParentCategory.querySelectorAll('li').forEach(el => el.classList.remove('active'));
        target.classList.add('active');

        const parentCode = target.getAttribute('data-code');
        const parentName = target.innerText.trim();

        // parentCode가 빈값이면 "전체" 선택된 것으로 처리
        selectedCategoryCode = parentCode || "";
        selectedCategoryName = parentCode ? parentName : "카테고리 전체";

        listChildCategory.innerHTML = `<li class="active" data-code="${parentCode || ''}" data-name="${selectedCategoryName}">전체</li>`;

        if (!parentCode) return;

        const subCategories = allCategories.filter(cat => String(cat.parentCategoryCode) === String(parentCode));
        subCategories.forEach(sub => {
            const li = document.createElement('li');
            li.setAttribute('data-code', sub.categoryCode);
            li.setAttribute('data-name', sub.categoryName);
            li.textContent = sub.categoryName;
            listChildCategory.appendChild(li);
        });
    });

    listChildCategory?.addEventListener('click', function(e) {
        const target = e.target.closest('li');
        if (!target) return;

        listChildCategory.querySelectorAll('li').forEach(el => el.classList.remove('active'));
        target.classList.add('active');

        selectedCategoryCode = target.getAttribute('data-code') || "";
        selectedCategoryName = target.getAttribute('data-name') || target.innerText.trim();
    });

    btnCategorySearch?.addEventListener('click', function(e) {
        e.stopPropagation();
        categoryModal?.classList.add('hidden');

        const textSpan = btnCategoryToggle.querySelector('.select-text');
        if (textSpan) {
            textSpan.innerText = selectedCategoryCode ? selectedCategoryName : "카테고리 전체";
        }

        filterClassesByMapBounds();
    });

    // 외부 영역 클릭 시 전체 모달 닫기
    document.addEventListener('click', function(e) {
        if (regionModal && !regionModal.contains(e.target) && !btnRegionToggle.contains(e.target)) {
            regionModal.classList.add('hidden');
        }
        if (categoryModal && !categoryModal.contains(e.target) && !btnCategoryToggle.contains(e.target)) {
            categoryModal.classList.add('hidden');
        }
        if (dateModal && !dateModal.contains(e.target) && !btnDateToggle.contains(e.target)) {
            dateModal.classList.add('hidden');
        }
        if (timeModal && !timeModal.contains(e.target) && !btnTimeToggle.contains(e.target)) {
            timeModal.classList.add('hidden');
        }
    });

    // -------------------------------------------------------------
    // 8. 카드 클릭 시 상세 페이지 이동 이벤트 및 초기 실행
    // -------------------------------------------------------------
    const cardContainer = document.getElementById('card-list-container');

    if (cardContainer) {
        cardContainer.addEventListener('click', (e) => {
            if (e.target.closest('.wish-btn')) {
                e.stopPropagation();
                return;
            }

            const card = e.target.closest('.class-card');
            if (!card) return;

            const selectedId = card.dataset.id;
            console.log("선택한 클래스 ID:", selectedId);

            if (selectedId && selectedId !== 'undefined' && selectedId !== 'null') {
                window.location.href = `/classDetail?classCode=${selectedId}`;
            } else {
                console.error("클래스 ID를 읽어오지 못했습니다.");
            }
        });
    }

    // 초기 지도 범위 필터링 실행
    filterClassesByMapBounds();
});