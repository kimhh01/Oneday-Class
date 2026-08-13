/**
 * 맵 자바스크립트 (동그라미 커스텀 오버레이 마커 + 지도 영역 연동)
 */
document.addEventListener("DOMContentLoaded", function() {
	
	// 💡 전역 데이터 출력
    console.log("전체 데이터 목록:", classListData);
    
    // -------------------------------------------------------------
    // 1. 카카오 지도 생성 및 전역 변수
    // -------------------------------------------------------------
    const mapContainer = document.getElementById('map');
    let allClassData = typeof classListData !== 'undefined' ? classListData : [];
    console.log("allClassData 개수:", allClassData.length);
    let markers = []; // 생성된 커스텀 오버레이 객체 저장 배열

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
	    '1': { icon: '/map/images/free.png', name: '요리/베이킹' },
	    '2': { icon: '/map/images/craft.png',   name: '공예' },
	    '3': { icon: '/images/category/flower.png',  name: '플라워' },
	    '4': { icon: '/images/category/candle.png',  name: '캔들' },
	    '5': { icon: '/images/category/beverage.png', name: '음료' },
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
	                width: 25px;
	                height: 25px;
	                background-color: #1B5E20;
	                border-radius: 50% 50% 50% 0;
	                transform: rotate(-45deg);
	                box-shadow: -2px 3px 6px rgba(0, 0, 0, 0.25);
	                display: flex;
	                align-items: center;
	                justify-content: center;
	                cursor: pointer;
	                transition: transform 0.2s ease;
	                overflow: hidden;
	            ">
	                <img src="${style.icon}" alt="${style.name}" style="
	                    transform: rotate(45deg);
	                    width: 15px;
	                    height: 15px;
	                    object-fit: contain;
	                    display: block;
	                    position: absolute;
	                "/>
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
    // 3. 지도의 현재 범위(Bounds) 필터링 & 마커/사이드바 렌더링
    // -------------------------------------------------------------
    function filterClassesByMapBounds() {
        const bounds = map.getBounds();
        const swLatLng = bounds.getSouthWest();
        const neLatLng = bounds.getNorthEast();

        const minLat = swLatLng.getLat();
        const maxLat = neLatLng.getLat();
        const minLng = swLatLng.getLng();
        const maxLng = neLatLng.getLng();

        const visibleClasses = allClassData.filter(item => {
            if (!item.lat || !item.lng) return false;
            return (item.lat >= minLat && item.lat <= maxLat && item.lng >= minLng && item.lng <= maxLng);
        });

        renderMarkersAndSidebar(visibleClasses);
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
                
                // 🔥 [해결 1] 타임리프와 동일하게 item.classCode (또는 대소문자 고려)를 추출
                const targetId = item.classCode || item.classcode || item.classNo || item.id;

                // 🔥 [해결 2] data-id="${targetId}" 부여
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

    kakao.maps.event.addListener(map, 'idle', function() {
        filterClassesByMapBounds();
    });

    // -------------------------------------------------------------
    // 4. 모달 및 기타 필터 이벤트
    // -------------------------------------------------------------
    const btnRegionToggle = document.getElementById('btn-region-toggle');
    const regionModal = document.getElementById('region-modal');
    const btnRegionCancel = document.getElementById('btn-region-cancel');
    const btnRegionSearch = document.getElementById('btn-region-search');
    const listSido = document.getElementById('list-sido');
    const listSigungu = document.getElementById('list-sigungu');
    const listDong = document.getElementById('list-dong');

    const btnCategoryToggle = document.getElementById('btn-category-toggle');
    const categoryModal = document.getElementById('category-modal');

    let selectedSido = "서울";
    let selectedSigungu = "";
    let selectedDong = "";

    btnRegionToggle?.addEventListener('click', (e) => {
        e.stopPropagation();
        categoryModal?.classList.add('hidden');
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

        selectedSido = target.getAttribute('data-sido') || target.innerText.trim();
        selectedSigungu = "";
        selectedDong = "";

        listDong.innerHTML = '<li class="active" data-dong="선택안함">선택안함</li>';

        ps.keywordSearch(`${selectedSido}`, function(data, status) {
            listSigungu.innerHTML = '<li class="active" data-sigungu="선택안함">선택안함</li>';
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

    btnRegionSearch?.addEventListener('click', function(e) {
        e.stopPropagation();
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
            } else {
                alert("해당 지역의 위치를 찾을 수 없습니다.");
            }
        });
    });

    const btnMyLocation = document.getElementById('btn-my-location');
    btnMyLocation?.addEventListener('click', function() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function(position) {
                const locPosition = new kakao.maps.LatLng(position.coords.latitude, position.coords.longitude);
                map.setCenter(locPosition);
                map.setLevel(4);
            });
        }
    });

    document.addEventListener('click', function(e) {
        if (regionModal && !regionModal.contains(e.target) && !btnRegionToggle.contains(e.target)) {
            regionModal.classList.add('hidden');
        }
    });

    // 초기 실행
    filterClassesByMapBounds();
	
    // -------------------------------------------------------------
    // 5. 🔥 카드 클릭 시 상세 페이지 이동 이벤트
    // -------------------------------------------------------------
	const cardContainer = document.getElementById('card-list-container');

	if (cardContainer) {
	    cardContainer.addEventListener('click', (e) => {
	        // 1. 관심목록(♡) 클릭 시 페이지 이동 방지
	        if (e.target.closest('.wish-btn')) {
	            e.stopPropagation();
	            return;
	        }

	        // 2. 가장 가까운 .class-card 요소 탐색
	        const card = e.target.closest('.class-card');
	        if (!card) return;

	        // 3. 카드에 할당된 data-id 읽기
	        const selectedId = card.dataset.id;
	        console.log("선택한 클래스 ID:", selectedId);

	        // 4. 유효성 체크 후 페이지 이동
	        if (selectedId && selectedId !== 'undefined' && selectedId !== 'null') {
	            window.location.href = `/classDetail?classCode=${selectedId}`;
	        } else {
	            console.error("클래스 ID를 읽어오지 못했습니다. 데이터 필드명을 확인하세요.");
	        }
	    });
	}
});