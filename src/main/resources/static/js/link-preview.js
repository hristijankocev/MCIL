$(document).ready(function ($) {
    /* ------------------------- */
    /* Contact Form Interactions */
    /* ------------------------- */
    let clickedLink = $('.preview-link');
    clickedLink.on('click', function () {
        let data = {
            linkId: clickedLink.attr("linkId")
        }

        addData(data);

        $('.contact').addClass('is-visible');
    });

    // close popup when clicking x or off popup
    $('.cd-popup').on('click', function (event) {
        if ($(event.target).is('.cd-popup-close') || $(event.target).is('.cd-popup')) {
            event.preventDefault();
            $(this).removeClass('is-visible');
        }
    });

    // close popup when clicking the esc keyboard button
    $(document).keyup(function (event) {
        if (event.which === '27') {
            $('.cd-popup').removeClass('is-visible');
        }
    });
});

function addData(data) {
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/webpage/preview",
        data: data,
        contentType: "application/json; charset=utf-8",
        crossDomain: false,
        dataType: "json",

        success: function (data) {
            console.log(data);
        },

        error: function (jqXHR, status) {
            console.log(status);
            console.log(jqXHR);
        }
    });
}