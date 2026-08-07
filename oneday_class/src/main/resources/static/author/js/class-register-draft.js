(function () {
	'use strict';

	document.addEventListener('DOMContentLoaded', function () {
		var form = document.querySelector('[data-draft-form]');
		if (!form) return;

		var dirty = false;
		var submitting = false;

		function markDirty() {
			if (!submitting) dirty = true;
		}

		form.addEventListener('input', markDirty);
		form.addEventListener('change', markDirty);
		form.addEventListener('click', function (event) {
			if (event.target.closest('button[type="button"]')) markDirty();
		});
		form.addEventListener('submit', function () {
			submitting = true;
			dirty = false;
		});

		document.addEventListener('click', function (event) {
			var link = event.target.closest('a[href]');
			if (!link || !dirty || link.target === '_blank'
					|| link.getAttribute('href').charAt(0) === '#') return;
			if (!window.confirm('저장하지 않은 변경사항은 사라집니다. 페이지를 나가시겠습니까?')) {
				event.preventDefault();
				return;
			}
			dirty = false;
		});

		window.addEventListener('beforeunload', function (event) {
			if (!dirty || submitting) return;
			event.preventDefault();
			event.returnValue = '';
		});
	});
}());
