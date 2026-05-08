package com.company.nomeprojeto.clientes.dto;

public class ClientesResumoDTO {

    private long total;
    private long ativos;
    private long inativos;

    public ClientesResumoDTO(long total, long ativos, long inativos) {
        this.total = total;
        this.ativos = ativos;
        this.inativos = inativos;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getAtivos() {
        return ativos;
    }

    public void setAtivos(long ativos) {
        this.ativos = ativos;
    }

    public long getInativos() {
        return inativos;
    }

    public void setInativos(long inativos) {
        this.inativos = inativos;
    }
}
