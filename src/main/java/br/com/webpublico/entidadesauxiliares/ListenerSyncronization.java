package br.com.webpublico.entidadesauxiliares;

import javax.transaction.Synchronization;

public abstract class ListenerSyncronization implements Synchronization {
    @Override
    public void beforeCompletion() {

    }

    @Override
    public void afterCompletion(int i) {

    }
}
