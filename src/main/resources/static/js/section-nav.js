$(document).ready(function() {
    // Check if we have a scroll target from login page
    const scrollTarget = sessionStorage.getItem('scrollTarget');
    if (scrollTarget) {
        const $section = $('#' + scrollTarget);
        if ($section.length) {
            // Clear the scroll target
            sessionStorage.removeItem('scrollTarget');
            
            // Force immediate scroll
            $('html, body').animate({
                scrollTop: $section.offset().top
            }, 0);
        }
    }
    
    // Handle hash-based navigation for direct links
    else if (window.location.hash) {
        const $section = $(window.location.hash);
        if ($section.length) {
            requestAnimationFrame(() => {
                window.scrollTo(0, $section.offset().top);
                
                // Update navigation
                const sectionNum = window.location.hash.replace('#section_', '') - 1;
                $('#sidebarMenu .nav-link').removeClass('active');
                $('#sidebarMenu .nav-link:link').addClass('inactive');
                $('#sidebarMenu .nav-item .nav-link').eq(sectionNum).addClass('active');
                $('#sidebarMenu .nav-item .nav-link').eq(sectionNum).removeClass('inactive');
            });
        }
    }
}); 