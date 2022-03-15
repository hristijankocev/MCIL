window.addEventListener('load', function () {
    $('#profile-picture').change(() => {
        $('#form-pfp').submit();
    })
});