/**
 * layout.js - Thymeleaf 공통 레이아웃의 화면 동작
 *
 * 아이콘/헤더/사이드바 마크업은 서버에서 Thymeleaf fragment로 조합한다.
 * 페이지 body의 data-menu와 data-page를 이용해 현재 메뉴와 브레드크럼을 표시한다.
 */
(function () {
	'use strict';

	document.addEventListener('DOMContentLoaded', onLayoutReady);

	/* 서버에서 조합된 공통 영역 초기화 */
	function onLayoutReady() {
		setActiveMenu();
		setBreadcrumb();
		bindSidebarToggle();
		bindUserMenu();
		document.dispatchEvent(new CustomEvent('layout:ready'));
	}

	/* 사이드바 현재 메뉴 활성화 */
	function setActiveMenu() {
		var key = document.body.getAttribute('data-menu');
		if (!key) { return; }
		var link = document.querySelector('.nav-link[data-menu-key="' + key + '"]');
		if (link) {
			link.classList.add('is-active');
			link.setAttribute('aria-current', 'page');
		}
	}

	/* 브레드크럼 현재 페이지명 */
	function setBreadcrumb() {
		var title = document.body.getAttribute('data-page');
		var slot = document.querySelector('[data-breadcrumb-current]');
		var breadcrumb = document.querySelector('.breadcrumb');
		if (title && slot) { slot.textContent = title; }
		if (breadcrumb && document.body.getAttribute('data-menu') === 'home') {
			breadcrumb.classList.add('is-home');
		}
	}

	function bindUserMenu() {
		var button = document.querySelector('[data-user-menu-button]');
		var panel = document.getElementById('authorUserMenu');
		if (!button || !panel) { return; }

		button.addEventListener('click', function () {
			var opened = panel.hidden;
			panel.hidden = !opened;
			button.setAttribute('aria-expanded', String(opened));
			if (opened) {
				var firstLink = panel.querySelector('a');
				if (firstLink) { firstLink.focus(); }
			}
		});

		document.addEventListener('click', function (event) {
			if (!event.target.closest('.user-menu')) { close(); }
		});
		document.addEventListener('keydown', function (event) {
			if (event.key === 'Escape' && !panel.hidden) {
				close();
				button.focus();
			}
		});

		function close() {
			panel.hidden = true;
			button.setAttribute('aria-expanded', 'false');
		}
	}

	/* 모바일 사이드바 열기/닫기 */
	function bindSidebarToggle() {
		var sidebar = document.getElementById('sidebar');
		var toggle = document.querySelector('[data-sidebar-toggle]');
		var backdrop = document.querySelector('[data-sidebar-backdrop]');
		var toggleLabel = toggle && toggle.querySelector('.sr-only');
		if (!sidebar || !toggle) { return; }

		syncToggleLabel();

		toggle.addEventListener('click', function () {
			if (window.innerWidth > 1024) {
				var collapsed = document.body.classList.toggle('sidebar-collapsed');
				toggle.setAttribute('aria-expanded', String(!collapsed));
				if (toggleLabel) { toggleLabel.textContent = collapsed ? '메뉴 펼치기' : '메뉴 접기'; }
				return;
			}

			var opened = sidebar.classList.toggle('is-open');
			if (backdrop) { backdrop.classList.toggle('is-open', opened); }
			toggle.setAttribute('aria-expanded', String(opened));
			if (toggleLabel) { toggleLabel.textContent = opened ? '메뉴 닫기' : '메뉴 열기'; }
			document.body.classList.toggle('sidebar-open', opened);
		});

		if (backdrop) {
			backdrop.addEventListener('click', close);
		}
		document.addEventListener('keydown', function (e) {
			if (e.key === 'Escape') { close(); }
		});

		function close() {
			var wasOpen = sidebar.classList.contains('is-open');
			sidebar.classList.remove('is-open');
			if (backdrop) { backdrop.classList.remove('is-open'); }
			toggle.setAttribute('aria-expanded', 'false');
			if (toggleLabel) { toggleLabel.textContent = '메뉴 열기'; }
			document.body.classList.remove('sidebar-open');
			if (wasOpen) { toggle.focus(); }
		}

		window.addEventListener('resize', function () {
			if (window.innerWidth > 1024) { close(); }
			syncToggleLabel();
		});

		function syncToggleLabel() {
			if (window.innerWidth > 1024) {
				var expanded = !document.body.classList.contains('sidebar-collapsed');
				toggle.setAttribute('aria-expanded', String(expanded));
				if (toggleLabel) { toggleLabel.textContent = expanded ? '메뉴 접기' : '메뉴 펼치기'; }
				return;
			}
			toggle.setAttribute('aria-expanded', String(sidebar.classList.contains('is-open')));
			if (toggleLabel) { toggleLabel.textContent = sidebar.classList.contains('is-open') ? '메뉴 닫기' : '메뉴 열기'; }
		}
	}
})();
