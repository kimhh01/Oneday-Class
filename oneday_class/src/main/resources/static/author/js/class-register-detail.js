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

	var materialList = document.getElementById('materialList');
	var materialAdd = document.getElementById('materialAdd');
	var materialEmpty = document.getElementById('materialEmpty');
	if (materialList && materialAdd) {
		function updateMaterialState() {
			var count = materialList.querySelectorAll('.material-edit-row').length;
			materialAdd.disabled = count >= 10;
			if (materialEmpty) { materialEmpty.hidden = count > 0; }
		}

		function appendMaterialRow() {
			if (materialList.querySelectorAll('.material-edit-row').length >= 10) { return; }
			var row = document.createElement('div');
			row.className = 'material-edit-row';

			var name = document.createElement('input');
			name.className = 'input';
			name.type = 'text';
			name.name = 'materialNameList';
			name.maxLength = 30;
			name.placeholder = '준비물 이름';
			name.required = true;

			var content = document.createElement('input');
			content.className = 'input';
			content.type = 'text';
			content.name = 'materialContentList';
			content.maxLength = 80;
			content.placeholder = '예: 개별 지참 / 수업에서 제공';
			content.required = true;

			var remove = document.createElement('button');
			remove.type = 'button';
			remove.setAttribute('aria-label', '준비물 삭제');
			remove.innerHTML = '<svg aria-hidden="true"><use href="#icon-x"></use></svg>';
			row.append(name, content, remove);
			materialList.appendChild(row);
			updateMaterialState();
			name.focus();
		}

		materialAdd.addEventListener('click', appendMaterialRow);
		materialList.addEventListener('click', function (event) {
			var button = event.target.closest('button');
			if (!button) { return; }
			button.closest('.material-edit-row').remove();
			updateMaterialState();
		});
		updateMaterialState();
	}

	var description = document.getElementById('workDescription');
	var descriptionCount = document.getElementById('workDescriptionCount');
	if (description && descriptionCount) {
		description.addEventListener('input', function () {
			descriptionCount.textContent = description.value.length;
		});
		descriptionCount.textContent = description.value.length;
	}

})();
