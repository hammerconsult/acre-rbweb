package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.util.List;

public class PageDTO<T> implements Serializable {

    private T[] content;
    private Integer currentPage;
    private Long totalElements;
    private Integer totalPages;

    public PageDTO() {
    }

    public PageDTO(T[] content, Integer currentPage, Long totalElements, Integer totalPages) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public T[] getContent() {
        return content;
    }

    public void setContent(T[] content) {
        this.content = content;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
