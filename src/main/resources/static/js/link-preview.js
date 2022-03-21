$(document).ready(function ($) {
    const loading = $('#loading');
    const linkInfo = $('#link-info');
    const infoFragment = $('.cd-popup');

    let clickedLink = $('.preview-link');
    clickedLink.on('click', function (event) {
        event.preventDefault();
        infoFragment.show();
        loading.show();
        linkInfo.hide()

        let data = {
            linkId: $(this).attr("linkId")
        }

        addData(data);

        $('.contact').addClass('is-visible');
    });

    // close popup when clicking x or off popup
    infoFragment.on('click', function (event) {
        if ($(event.target).is('.cd-popup-close') || $(event.target).is('.cd-popup')) {
            event.preventDefault();
            $(this).removeClass('is-visible');
        }
    });

    // close popup when clicking the esc keyboard button
    $(document).keyup(function (event) {
        if (event.key === "Escape") {
            infoFragment.removeClass('is-visible');
        }
    });

    function addData(data) {
        $.ajax({
            type: "GET",
            url: "/webpage/preview",
            data: data,
            contentType: "application/json; charset=utf-8",
            crossDomain: false,
            dataType: "json",

            success: function (data) {
                console.log(data);
                $('#loading').hide();
                $('#link-info').show();
                $('#link-title').text(data['ogTitle']);
                $('#link-description').text(data['ogDesc'] !== "" ? data['ogDesc'] : data['desc']);
                $('#link-img').attr('src', data['ogImage']);
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
        $('#link-img').removeAttr('src', '').removeAttr('style');
        $('#link-url').text('').removeAttr('style');
    }
});

