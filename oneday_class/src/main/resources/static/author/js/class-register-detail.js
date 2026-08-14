(function () {
	'use strict';

	function setupTokenInput(inputId, addId, listId, name, limit) {
		var input = document.getElementById(inputId);
		var list = document.getElementById(listId);
		var add = addId ? document.getElementById(addId) : null;
		if (!input || !list) return;

		function values() {
			return Array.from(list.querySelectorAll('input[type="hidden"]')).map(function (item) {
				return item.value.toLowerCase();
			});
		}

		function appendToken() {
			var value = input.value.trim().replace(/^#/, '');
			if (!value || values().length >= limit || values().includes(value.toLowerCase())) {
				input.focus();
				return;
			}
			var token = document.createElement('span');
			token.className = 'token';
			token.append(document.createTextNode(value));

			var button = document.createElement('button');
			button.type = 'button';
			button.setAttribute('aria-label', value + ' 삭제');
			button.innerHTML = '<svg aria-hidden="true"><use href="#icon-x"></use></svg>';

			var hidden = document.createElement('input');
			hidden.type = 'hidden';
			hidden.name = name;
			hidden.value = value;
			token.append(button, hidden);
			list.appendChild(token);
			input.value = '';
			input.focus();
		}

		if (add) add.addEventListener('click', appendToken);
		input.addEventListener('keydown', function (event) {
			if (event.key !== 'Enter') return;
			event.preventDefault();
			appendToken();
		});
		list.addEventListener('click', function (event) {
			var button = event.target.closest('button');
			if (!button) return;
			button.closest('.token').remove();
			input.focus();
		});
	}

	setupTokenInput('benefitInput', 'benefitAdd', 'benefitList', 'advantageList', 5);
	setupTokenInput('audienceInput', 'audienceAdd', 'audienceList', 'recommendList', 5);
	setupTokenInput('offeringNote', 'noticeAdd', 'noticeList', 'noticeList', 5);
	setupTokenInput('tagInput', null, 'tagList', 'tagList', 8);

	var description = document.getElementById('workDescription');
	var descriptionCount = document.getElementById('workDescriptionCount');
	if (description && descriptionCount) {
		description.addEventListener('input', function () {
			descriptionCount.textContent = description.value.length;
		});
		descriptionCount.textContent = description.value.length;
	}

	var detailForm = document.getElementById('detailForm');
	if (!detailForm) return;

	function appendRemoveCode(imageCode) {
		if (detailForm.querySelector('input[name="removeImageCodeList"][value="' + imageCode + '"]')) return;
		var hidden = document.createElement('input');
		hidden.type = 'hidden';
		hidden.name = 'removeImageCodeList';
		hidden.value = imageCode;
		detailForm.appendChild(hidden);
	}

	function setupImageUploader(config) {
		var grid = document.getElementById(config.gridId);
		var input = document.getElementById(config.inputId);
		var count = document.getElementById(config.countId);
		if (!grid || !input || !count) return;

		var clear = config.clearId ? document.getElementById(config.clearId) : null;
		var placeholder = grid.querySelector('[data-upload-placeholder]');
		var selectedFiles = [];

		function existingCount() {
			return grid.querySelectorAll('.work-card.is-filled[data-existing-image]').length;
		}

		function syncInput() {
			var transfer = new DataTransfer();
			selectedFiles.forEach(function (file) { transfer.items.add(file); });
			input.files = transfer.files;
		}

		function updateState() {
			var total = existingCount() + selectedFiles.length;
			count.textContent = total + ' / ' + config.max;
			if (placeholder) placeholder.hidden = total >= config.max;
			if (config.recommendationId) {
				var recommendation = document.getElementById(config.recommendationId);
				if (recommendation) recommendation.hidden = total >= 3;
			}
		}

		function renderSelectedFiles() {
			grid.querySelectorAll('[data-new-file-index]').forEach(function (card) { card.remove(); });
			selectedFiles.forEach(function (file, index) {
				var card = document.createElement('article');
				card.className = 'work-card is-filled' + (config.compact ? ' work-card--compact' : '');
				card.dataset.newFileIndex = String(index);

				var media = document.createElement('div');
				media.className = 'work-card__media';
				var img = document.createElement('img');
				img.src = URL.createObjectURL(file);
				img.alt = '새로 등록한 ' + config.label + ' 이미지';
				var remove = document.createElement('button');
				remove.className = 'work-card__remove';
				remove.type = 'button';
				remove.setAttribute('aria-label', config.label + ' 사진 삭제');
				remove.innerHTML = '<svg aria-hidden="true"><use href="#icon-x"></use></svg>';
				media.append(img, remove);
				card.append(media);

				if (!config.compact) {
					var label = document.createElement('label');
					var title = document.createElement('input');
					title.type = 'text';
					title.value = file.name;
					title.readOnly = true;
					title.setAttribute('aria-label', '새 ' + config.label + ' 파일명');
					var status = document.createElement('small');
					status.textContent = '새 이미지';
					label.append(title, status);
					card.append(label);
				}
				grid.insertBefore(card, placeholder);
			});
			updateState();
		}

		grid.addEventListener('click', function (event) {
			var remove = event.target.closest('.work-card__remove');
			if (!remove) return;
			var card = remove.closest('.work-card');
			if (card.dataset.existingImage) {
				appendRemoveCode(card.dataset.existingImage);
				card.remove();
				updateState();
				return;
			}
			selectedFiles.splice(Number(card.dataset.newFileIndex), 1);
			syncInput();
			renderSelectedFiles();
		});

		input.addEventListener('change', function () {
			var available = Math.max(0, config.max - existingCount() - selectedFiles.length);
			var added = Array.from(input.files).filter(function (file) {
				return (file.type === 'image/jpeg' || file.type === 'image/png')
						&& file.size <= 5 * 1024 * 1024;
			});
			var known = new Set(selectedFiles.map(function (file) {
				return file.name + ':' + file.size + ':' + file.lastModified;
			}));
			added.filter(function (file) {
				var key = file.name + ':' + file.size + ':' + file.lastModified;
				if (known.has(key)) return false;
				known.add(key);
				return true;
			}).slice(0, available).forEach(function (file) { selectedFiles.push(file); });
			syncInput();
			renderSelectedFiles();
		});

		if (clear) {
			clear.addEventListener('click', function () {
				grid.querySelectorAll('[data-existing-image]').forEach(function (card) {
					appendRemoveCode(card.dataset.existingImage);
					card.remove();
				});
				selectedFiles = [];
				syncInput();
				renderSelectedFiles();
			});
		}

		updateState();
	}

	setupImageUploader({
		gridId: 'resultGrid', inputId: 'resultInput', countId: 'resultCount',
		max: 4, label: '완성작'
	});
	setupImageUploader({
		gridId: 'galleryGrid', inputId: 'galleryInput', countId: 'galleryCount',
		clearId: 'galleryClear', recommendationId: 'galleryRecommendation',
		max: 9, label: '갤러리', compact: true
	});
})();
