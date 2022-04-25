'use strict';

var reader = new FileReader();

reader.onload = function(r_event) {
  document.getElementById('prev').setAttribute('src', r_event.target.result);
}

document.getElementsByName('file').addEventListener('change', function(event) {
    reader.readAsDataURL(this.files);
});