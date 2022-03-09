$(document).ready(function ($) {
    /* ------------------------- */
    /* Contact Form Interactions */
    /* ------------------------- */
    let newWorkplaceInput = $('#new-workplace');
    let newWorkplaceDiv = $('.new-workplace');
    let contactForm = $('#contact-form');

    $('#contact').on('click', function (event) {
        event.preventDefault();

        $('.contact').addClass('is-visible');

        if (newWorkplaceInput.val().length !== 0) {
            newWorkplaceDiv.addClass('typing');
        }
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

    /* ------------------- */
    /* Contact Form Labels */
    /* ------------------- */
    newWorkplaceInput.keyup(function () {
        newWorkplaceDiv.addClass('typing');
        if ($(this).val().length === 0) {
            newWorkplaceDiv.removeClass('typing');
        }
    });
    /* ----------------- */
    /* Handle submission */
    /* ----------------- */
    contactForm.submit(function () {
            let name = newWorkplaceInput.val();

            if (name) {
                // Ajax call to add workplace to the current logged user
                closeForm();
            } else {
                $('#notification-text').html('<strong>Please provide a workplace.</strong>');
                $('.notification').addClass('is-visible');
                return false;
            }
            return false;
        }
    );
});

function closeForm() {
    $('#new-workplace').val('');
    $('.cd-popup').removeClass('is-visible');
    $('.notification').addClass('is-visible');
    $('#notification-text').html("Thanks for contacting us!");
}