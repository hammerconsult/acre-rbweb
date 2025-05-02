package br.com.webpublico.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProcessAsync {

    private final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    private String id;
    private LocalDateTime inicio;
    private DetailProcessAsync detailProcessAsync;
    private CompletableFuture completableFuture;

    public ProcessAsync() {
        this.id = UUID.randomUUID().toString();
        this.inicio = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public DetailProcessAsync getDetailProcessAsync() {
        return detailProcessAsync;
    }

    public void setDetailProcessAsync(DetailProcessAsync detailProcessAsync) {
        this.detailProcessAsync = detailProcessAsync;
    }

    public CompletableFuture getCompletableFuture() {
        return completableFuture;
    }

    public void setCompletableFuture(CompletableFuture completableFuture) {
        this.completableFuture = completableFuture;
    }

    public String getInicioExecucao() {
        return inicio.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public String formatSeconds(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String getTempoExecucao() {
        long totalSecs = ChronoUnit.SECONDS.between(inicio, LocalDateTime.now());
        return formatSeconds(totalSecs);
    }

    public void stopProcessAsync() {
        if (completableFuture != null) {
            completableFuture.complete(null);
        }
    }

}
