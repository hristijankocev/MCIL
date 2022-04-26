$(document).ready(function ($) {
    const loading = $('#loading');
    const linkInfo = $('#link-info');
    const infoFragment = $('.cd-popup');

    let clickedLink = $('.preview-link');
    clickedLink.on('click', function () {
        let linkStr = $(this).attr('linkString');
        let authUrlHelper = $('#auth-url-helper');
        let authUlrIdHelper = $('#auth-url-id-helper');

        authUrlHelper.val(linkStr);
        authUlrIdHelper.val($(this).attr('linkId'));

        if (linkStr.toLowerCase().indexOf('linkedin') >= 0) {
            // If the url contains 'LinkedIn' ask the user whether he'd like to supply authentication details
            // and handle the modal's buttons correctly
            $('#auth-modal-btn').click();
            return;
        }

        let data = {
            linkId: $(this).attr('linkId')
        }
        // Do the rest
        initiateCall(data);
    });


    // If the user chooses to use the credentials
    $('#auth-use-credentials').click(() => {
        let data = {
            linkId: $('#auth-url-id-helper').val(),
            sessionKey: $('input[name="sessionKey"]').val(),
            sessionPassword: $('input[name="sessionPassword"]').val()
        }
        initiateCall(data);
    });

    // If the user wishes to proceed without credentials
    $('#auth-no-credentials').click(() => {
        let data = {
            linkId: $('#auth-url-id-helper').val()
        }
        initiateCall(data);
    });

    function initiateCall(data) {
        infoFragment.show();
        loading.show();
        linkInfo.hide()

        addData(data);

        $('.contact').addClass('is-visible');
    }

    // close popup when clicking x or off popup
    infoFragment.on('click', function (event) {
        if ($(event.target).is('.cd-popup-close') || $(event.target).is('.cd-popup')) {
            event.preventDefault();
            clearFields();
            $(this).removeClass('is-visible');
        }
    });

    // close popup when clicking the esc keyboard button
    $(document).keyup(function (event) {
        if (event.key === 'Escape') {
            clearFields();
            infoFragment.removeClass('is-visible');
        }
    });

    function addData(data) {
        $.ajax({
            type: 'GET',
            url: '/webpage/preview',
            data: data,
            contentType: 'application/json; charset=utf-8',
            crossDomain: false,
            dataType: 'json',

            success: function (data) {
                console.log(data);
                $('#loading').hide();
                $('#link-info').show();
                $('#link-title').text(data['ogTitle'] !== '' ? data['ogTitle'] : data['title']);
                $('#link-description').text(data['ogDesc'] !== '' ? data['ogDesc'] : data['desc']);
                $('#link-img').attr('src', data['ogImage']);
                $('#external-pic-url').val(data['ogImage']);
                $('#image-link')
                    .text(data['ogImage'])
                    .attr('href', data['ogImage']);
                $('#link-url')
                    .text(data['ogUrl'])
                    .attr('href', data['ogUrl']);
            },

            error: function (jqXHR, status) {
                clearFields()
                $('#link-title')
                    .css({'color': 'red'})
                    .text('Failed to fetch data. Status: ' + jqXHR.responseJSON.status);
                $('#link-description').text(jqXHR.responseJSON.message);
                $('#loading').hide();
                $('#link-info').show();
                console.log(jqXHR);
            }
        });
    }

    // Reset field values
    function clearFields() {
        $('#link-title').text('').removeAttr('style');
        $('#link-description').text('').removeAttr('style');
        $('#link-img').removeAttr('src', '');
        $('#link-url').text('').removeAttr('style');
    }
});

