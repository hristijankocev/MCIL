// function confirmWorkplaceDel() {
//     let form = document.getElementById('form-del-workplace');
//     console.log(form);
//     if (confirm('Are you sure you want to delete this workplace for the current user?')) {
//         form.submit();
//         console.log('truue');
//         return true;
//     } else {
//         console.log('falsed');
//         return false;
//     }
// }

window.addEventListener('load', function () {
    $('#profile-picture').change(() => {
        $('#form-pfp').submit();
    })
});