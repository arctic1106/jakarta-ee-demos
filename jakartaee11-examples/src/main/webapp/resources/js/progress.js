topbar.config({
    autoRun: true,
    barThickness: 3,
    barColors: {
        '0.75': 'rgba(255, 212, 0, .9)',
        '1.0':  'rgba(211, 84,  0, .9)'
    },
    shadowBlur: 10,
    shadowColor: 'rgba(0, 0, 0, .6)',
    className: 'topbar'
});

topbar.show();
setTimeout(function () { topbar.hide(); }, 800);
