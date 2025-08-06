(function ($) {
  
  "use strict";

    // Handle login button click with smooth transition
    $('a[href="/login"]').on('click', function(e) {
      e.preventDefault();
      
      // Add transition class and fade out
      $('body').addClass('login-transition fade-out');
      setTimeout(function() {
        window.location.href = '/login';
      }, 600);
    });
    
    // Note: Removed conflicting smoothscroll handler - now handled in index.html
    
  })(window.jQuery);


