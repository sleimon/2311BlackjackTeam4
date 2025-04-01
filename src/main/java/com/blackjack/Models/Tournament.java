package com.blackjack.Models;

import java.sql.Timestamp;

public class Tournament {
    private int tournamentId;
    private String name;
    private int targetChips;
    private int activePlayers;
    private String status;
    private int wonBy;
    private Timestamp createdAt;

    public Tournament(int tournamentId, String name, int targetChips, int activePlayers, String status, int wonBy, Timestamp createdAt) {
        this.tournamentId = tournamentId;
        this.name = name;
        this.targetChips = targetChips;
        this.activePlayers = activePlayers;
        this.status = status;
        this.wonBy = wonBy;
        this.createdAt = createdAt;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public String getName() {
        return name;
    }

    public int getTargetChips() {
        return targetChips;
    }

    public int getActivePlayers() {
        return activePlayers;
    }

    public String getStatus() {
        return status;
    }

    public int getWonBy() {
        return wonBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "gameId=" + tournamentId +
                ", name='" + name + '\'' +
                ", targetChips=" + targetChips +
                ", activePlayers=" + activePlayers +
                ", status='" + status + '\'' +
                ", wonBy='" + wonBy + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
