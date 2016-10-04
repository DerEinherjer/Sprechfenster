/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster.Presenters;

import Model.iTournament;

/**
 *
 * @author Stefan
 */
public class TournamentPresenter {
    
    private iTournament Tournament;
    
    public TournamentPresenter(iTournament tournamentToPresent)
    {
        this.Tournament = tournamentToPresent;
    }
    
    public iTournament getTournament()
    {
        return Tournament;
    }
    
    public String getName()
    {
        return Tournament.getName();
    }
    
    public String getDate()
    {
        return Tournament.getDate();
    }
}
