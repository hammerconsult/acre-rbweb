$(document).ready(function () {
    var here = location.href.split('/').slice(3);

    $("#breadcrumb-webpublico").append(' <li>\n' +
        '        <a href="/">\n' +
        '        Home\n' +
        '        </a>\n' +
        '        </li>');
    var parts = [];
    for (var i = 0; i < here.length - 1; i++) {
        var part = here[i];
        var text = part.split("-").join(" ").capitalizeFirstLetter();
        $("#breadcrumb-webpublico").append(' <li><a href="#"><span class="divider">&rsaquo;&rsaquo;&nbsp;</span>&nbsp;' + text + '&nbsp;</a></li>');
        parts.push(text);
    }

    if(parts[0] !== undefined){
        document.title = 'WP - ' + parts[0];
    }

});
