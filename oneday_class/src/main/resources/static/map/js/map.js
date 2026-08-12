/**
 * 맵 자바스크립트 통합본
 */
document.addEventListener("DOMContentLoaded", function () {

    // -------------------------------------------------------------
    // 1. 카카오 지도 생성 및 마커 표시
    // -------------------------------------------------------------
    var mapContainer = document.getElementById('map'); 
    
    console.log("전달받은 클래스 목록:", typeof classListData !== 'undefined' ? classListData : []);

    // 기본 중심 좌표 (서울시청)
    var defaultLat = 37.5381;
    var defaultLng = 127.11609;

    if (typeof classListData !== 'undefined' && classListData.length > 0 && classListData[0].lat && classListData[0].lng) {
        defaultLat = classListData[0].lat;
        defaultLng = classListData[0].lng;
    }

    var mapOption = {
        center: new kakao.maps.LatLng(defaultLat, defaultLng),
        level: 3
    };

    // 💡 전역 지도 객체 생성
    var map = new kakao.maps.Map(mapContainer, mapOption);

    // 마커 이미지 설정
    var imageSrc = "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png";
    var imageSize = new kakao.maps.Size(24, 35);
    var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);

    // 동적 마커 생성
    if (typeof classListData !== 'undefined' && classListData.length > 0) {
        var bounds = new kakao.maps.LatLngBounds();
        var hasValidMarker = false;

        classListData.forEach(function (item) {
            if (item.lat && item.lng) {
                var latlng = new kakao.maps.LatLng(item.lat, item.lng);

                var marker = new kakao.maps.Marker({
                    map: map,
                    position: latlng,
                    title: item.name,
                    image: markerImage 
                });

                bounds.extend(latlng);
                hasValidMarker = true;
            }
        });

        if (hasValidMarker) {
            map.setBounds(bounds);
        }
    }


    // -------------------------------------------------------------
    // 2. [지역 모달] 제어 및 지도 이동
    // -------------------------------------------------------------
    const btnRegionToggle = document.getElementById('btn-region-toggle');
    const regionModal = document.getElementById('region-modal');
    const btnRegionCancel = document.getElementById('btn-region-cancel');
    const btnRegionSearch = document.getElementById('btn-region-search');

    let selectedSido = "서울";
    let selectedSigungu = "";
    let selectedDong = "";

    // 지역 모달 열기
    btnRegionToggle?.addEventListener('click', () => {
        regionModal.classList.toggle('hidden');
        categoryModal.classList.add('hidden'); // 카테고리 모달은 닫음
    });

    // 지역 모달 닫기
    btnRegionCancel?.addEventListener('click', () => {
        regionModal.classList.add('hidden');
    });

    // 시/도 선택 이벤트
    document.querySelectorAll('#list-sido li').forEach(item => {
        item.addEventListener('click', function() {
            document.querySelectorAll('#list-sido li').forEach(el => el.classList.remove('active'));
            this.classList.add('active');
            selectedSido = this.getAttribute('data-sido') || "서울";
        });
    });

    // 구/군 선택 이벤트
    document.querySelectorAll('#list-sigungu li').forEach(item => {
        item.addEventListener('click', function() {
            document.querySelectorAll('#list-sigungu li').forEach(el => el.classList.remove('active'));
            this.classList.add('active');
            
            selectedSigungu = this.getAttribute('data-sigungu');
            if (selectedSigungu === '선택안함') selectedSigungu = '';
        });
    });

    // 동/읍/면 선택 이벤트
    document.querySelectorAll('#list-dong li').forEach(item => {
        item.addEventListener('click', function() {
            document.querySelectorAll('#list-dong li').forEach(el => el.classList.remove('active'));
            this.classList.add('active');
            
            selectedDong = this.getAttribute('data-dong');
            if (selectedDong === '선택안함') selectedDong = '';
        });
    });

    // 지역 '검색' 버튼 클릭 시 지도 이동
    var geocoder = new kakao.maps.services.Geocoder();

    btnRegionSearch?.addEventListener('click', function () {
        let fullAddress = `${selectedSido} ${selectedSigungu} ${selectedDong}`.trim();

        if (!fullAddress) {
            alert("지역을 선택해주세요.");
            return;
        }

        geocoder.addressSearch(fullAddress, function (result, status) {
            if (status === kakao.maps.services.Status.OK) {
                var coords = new kakao.maps.LatLng(result[0].y, result[0].x);

                map.setCenter(coords);
                
                if (selectedDong) {
                    map.setLevel(4);
                } else if (selectedSigungu) {
                    map.setLevel(6);
                } else {
                    map.setLevel(8);
                }

                regionModal.classList.add('hidden');
                btnRegionToggle.innerText = `${selectedSigungu || selectedSido} ▼`;

            } else {
                alert("해당 지역의 위치 정보를 찾을 수 없습니다.");
            }
        });
    });


    // -------------------------------------------------------------
    // 3. [카테고리 모달] 제어 및 필터링
    // -------------------------------------------------------------
    const btnCategoryToggle = document.getElementById('btn-category-toggle');
    const categoryModal = document.getElementById('category-modal');
    const btnCategoryCancel = document.getElementById('btn-category-cancel');
    const btnCategorySearch = document.getElementById('btn-category-search');

    let selectedCategory = "";

    // 카테고리 모달 열기
    btnCategoryToggle?.addEventListener('click', () => {
        categoryModal.classList.toggle('hidden');
        regionModal.classList.add('hidden'); // 지역 모달은 닫음
    });

    // 카테고리 모달 닫기
    btnCategoryCancel?.addEventListener('click', () => {
        categoryModal.classList.add('hidden');
    });

	// 카테고리 항목 클릭 선택 (기존과 동일하게 정상 동작!)
	document.querySelectorAll('#list-category li').forEach(item => {
	    item.addEventListener('click', function() {
	        // 모든 li의 active 클래스 제거 후 클릭한 항목에만 추가
	        document.querySelectorAll('#list-category li').forEach(el => el.classList.remove('active'));
	        this.classList.add('active');
	        
	        // 클릭한 카테고리 값 저장
	        selectedCategory = this.getAttribute('data-category');
	    });
	});

	// 카테고리 '검색' 버튼 클릭 시
	const btnCategorySearch = document.getElementById('btn-category-search');
	btnCategorySearch?.addEventListener('click', function () {
	    
	    // 버튼 텍스트 변경 (비어있으면 '카테고리', 있으면 선택한 카테고리명)
	    btnCategoryToggle.innerText = `${selectedCategory || '카테고리'} ▼`;
	    categoryModal.classList.add('hidden');

	    console.log("선택된 카테고리:", selectedCategory);

	    // 💡 선택된 카테고리로 백엔드에 검색 요청을 보내는 방법 2가지:
	    // 방법 A. URL 이동 (쿼리 파라미터)
	    // window.location.href = `/map?category=${encodeURIComponent(selectedCategory)}`;

	    // 방법 B. AJAX(fetch)로 데이터만 갱신 후 지도/마커 새로고침
	    
	    fetch(`/api/map/classes?category=${encodeURIComponent(selectedCategory)}`)
	        .then(res => res.json())
	        .then(data => {
	            // 받아온 데이터로 마커 및 사이드바 카드 목록 갱신
	        });
	   
	});

});