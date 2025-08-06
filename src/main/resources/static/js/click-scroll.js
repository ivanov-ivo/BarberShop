//jquery-click-scroll
//by syamsul'isul' Arifin

$(document).ready(function() {
    console.log('Click-scroll.js loaded (simplified version)');
    
    // Handle navigation from login page using sessionStorage
    const scrollTarget = sessionStorage.getItem('scrollTarget');
    if (scrollTarget) {
        const targetSection = $('#' + scrollTarget);
        if (targetSection.length) {
            $('html, body').animate({
                scrollTop: targetSection.offset().top
            }, 800);
        }
        // Clean up
        sessionStorage.removeItem('scrollTarget');
    }
    
    // Handle hash-based navigation
    else if (window.location.hash) {
        const sectionId = window.location.hash;
        const targetSection = $(sectionId);
        if (targetSection.length) {
            $('html, body').animate({
                scrollTop: targetSection.offset().top
            }, 800);
        }
    }
    
    // Note: Menu click handling is now managed in index.html for better control
    console.log('Click-scroll.js initialization complete');
});