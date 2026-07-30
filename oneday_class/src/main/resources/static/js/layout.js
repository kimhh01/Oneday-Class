/**
 * layout.js - 공통 레이아웃(아이콘/헤더/사이드바) 로더
 *
 * [사용법]
 *  1) 페이지 body 에 data-menu(사이드바 활성 메뉴), data-page(브레드크럼 텍스트) 지정
 *     <body data-menu="home" data-page="홈">
 *  2) 공통 영역 자리에 placeholder 를 둡니다.
 *     <div data-include="../includes/icons.html"></div>
 *     <div data-include="../includes/header.html"></div>
 *     <div data-include="../includes/sidebar.html"></div>
 *
 * [주의] fetch 를 쓰기 때문에 file:// 로 직접 열면 동작하지 않습니다.
 *        톰캣(또는 VS Code Live Server) 로 띄워서 확인하세요.
 *
 * [구조] 화면은 정적 HTML/CSS/JS 로 구성하고 서버 데이터는 REST API 로 받아옵니다.
 *        공통 헤더/사이드바는 이 로더로 재사용합니다. (AGENTS.md 3, 17장)
 */
(function () {
	'use strict';

	document.addEventListener('DOMContentLoaded', function () {
		var targets = [].slice.call(document.querySelectorAll('[data-include]'));
		loadNext(0);

		/* DOM 순서대로 하나씩 삽입 (아이콘 스프라이트가 먼저 들어가야 <use> 가 그려짐) */
		function loadNext(index) {
			if (index >= targets.length) {
				onLayoutReady();
				return;
			}
			var el = targets[index];
			var url = el.getAttribute('data-include');

			fetch(url)
				.then(function (res) {
					if (!res.ok) { throw new Error('HTTP ' + res.status); }
					return res.text();
				})
				.then(function (html) {
					el.outerHTML = html;
				})
				.catch(function (err) {
					console.error('[layout] include 실패 : ' + url, err);
					console.info('[layout] file:// 로 열면 include 가 막힙니다. 서버로 띄워주세요.');
				})
				.then(function () {
					loadNext(index + 1);
				});
		}
	});

	/* 공통 영역 삽입 완료 후 처리 */
	function onLayoutReady() {
		setActiveMenu();
		setBreadcrumb();
		bindSidebarToggle();
		document.dispatchEvent(new CustomEvent('layout:ready'));
	}

	/* 사이드바 현재 메뉴 활성화 */
	function setActiveMenu() {
		var key = document.body.getAttribute('data-menu');
		if (!key) { return; }
		var link = document.querySelector('.nav-link[data-menu-key="' + key + '"]');
		if (link) { link.classList.add('is-active'); }
	}

	/* 브레드크럼 현재 페이지명 */
	function setBreadcrumb() {
		var title = document.body.getAttribute('data-page');
		var slot = document.querySelector('[data-breadcrumb-current]');
		if (title && slot) { slot.textContent = title; }
	}

	/* 모바일 사이드바 열기/닫기 */
	function bindSidebarToggle() {
		var sidebar = document.getElementById('sidebar');
		var toggle = document.querySelector('[data-sidebar-toggle]');
		var backdrop = document.querySelector('[data-sidebar-backdrop]');
		if (!sidebar || !toggle) { return; }

		toggle.addEventListener('click', function () {
			var opened = sidebar.classList.toggle('is-open');
			if (backdrop) { backdrop.classList.toggle('is-open', opened); }
		});

		if (backdrop) {
			backdrop.addEventListener('click', close);
		}
		document.addEventListener('keydown', function (e) {
			if (e.key === 'Escape') { close(); }
		});

		function close() {
			sidebar.classList.remove('is-open');
			if (backdrop) { backdrop.classList.remove('is-open'); }
		}
	}
})();
