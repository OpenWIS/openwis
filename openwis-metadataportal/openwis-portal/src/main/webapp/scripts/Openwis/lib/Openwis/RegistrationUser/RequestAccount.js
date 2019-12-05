/**
 * JavaScript for account request.
 */

$(function() {

    /**
     * List of validation methods.
     */
    var VALIDATION_METHODS = {
        
        /**
         * True if the element has a value
         */
        required: function(el) {
            return ((el.val() != null) && (el.val().trim() != ""));
        },
    
        /**
         * True if the element is an email address.  This imples "required".
         */
        isEmail: function(el) {
            if (VALIDATION_METHODS.required(el)) {
                var value = el.val().trim();
                return /\S+@\S+/.test(value);
            } else {
                return false;
            }
        }
    };   
    
    /**
     * Global which would be set to true while the form is being submitted.  Used to disable the submit button
     * regardless of the current validation state of the form.
     */
    var formBeingSubmitted = false;
    
    /**
     * Returns true if all elements with a 'data-validation' attribute is valid.
     */
    var validate = function() {
        var isValid = true;
        $("*[data-validation]").each(function() {
            var validationFn = VALIDATION_METHODS[$(this).attr("data-validation")];
            if (validationFn != null) {
                isValid = isValid && validationFn($(this));
            }
        });
        return isValid;
    };
    
    /**
     * Updates the state of the submit button.
     */
    var updateDisabledState = function() {
        $("#submit").prop("disabled", formBeingSubmitted);
    };
    
    /**
     * Sends the form.
     */
    var sendForm = function(form) {
        var postData = $(form).serialize()
        
        formBeingSubmitted = true;
        updateDisabledState();        
        
        $("#ajax-loader").css({display: "inline"});
        $.post($(form).attr("action"), postData, function(resp) {
            $("#ajax-loader").css({display: "none"});
            
            if (resp.ok) {
                // Redirect to the page specified by the server
                location.replace(resp.redirectUrl);
            } else {
                // Display the alert and refresh the captcha.
                alert(resp.message);
                
                $("#captcha-image").attr("src", "captcha.png#" + new Date().getTime());
                $("#captcha").val("");
            }
                        
            formBeingSubmitted = false;
            updateDisabledState();
        });
    };

    
    
    /**
     * Setup the event handler for the onblur.
     */
    $("body").delegate("*[data-validation]", "change", function() {
        updateDisabledState();
    });
    
    
    /**
     * Submit the form using AJAX.
     */
    $("form").submit(function() {    
        if (! validate()) {
            alert("Please make sure you have filled in all fields before submitting.");
        } else {            
            sendForm($(this));
        }
        
        return false;
    });
    
    
    /**
     * Runs an initial validation check and clear the captcha field.
     */
    updateDisabledState();
    
    $("#captcha").val("");
    
    $("#firstname")[0].focus();
});