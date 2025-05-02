$(document).ready(function () {
    carregarJobsDoResource();
});

function carregarJobsDoResource() {
    const xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", getRequestContextPath() + '/spring/usuario-sistema/jobs', false);
    xmlHttp.send(null);
    const jobs = JSON.parse(xmlHttp.responseText);
    popularTabelaJobs(jobs);
}

function popularTabelaJobs(jobs) {
    $("#lista-jobs-sistema").empty();
    for (let i = 0; i < jobs.length; i++) {
        const job = jobs[i];
        const html = '<li><a href="" class="btn-small"> <i class="icon-play"></i>' + job.descricao + '</a></li>';
        $("#lista-jobs-sistema").append(html);
    }
    if (jobs.length === 0) {
        $("#li-jobs-sistema").css('display', 'none');
    } else {
        $("#icone-jobs-sistema").addClass("icon-animated-bell");
    }
}
