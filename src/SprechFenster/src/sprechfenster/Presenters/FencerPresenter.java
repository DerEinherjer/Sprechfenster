/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster.Presenters;

import Model.iFencer;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Stefan
 */
public class FencerPresenter {
    
    private iFencer Fencer;
    
    public FencerPresenter(iFencer fencerToPresent)
    {
        if(fencerToPresent == null)
        {
            throw new IllegalArgumentException("fencerToPresent must not be null");
        }
        Fencer = fencerToPresent;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(other == this)
        {
            return true;
        }
        else
        {
            if(!(other instanceof FencerPresenter))
            {
                return false;
            }
            else
            {
                return Fencer.equals(((FencerPresenter)other).Fencer);
            }
        }
    }
    
    @Override 
    public int hashCode()
    {
        return Fencer.hashCode();
    }
    
    public iFencer getFencer()
    {
        return Fencer;
    }
    
    public String getFullName()
    {
        return Fencer.getFullName();
    }
    
    public String getFencingSchool()
    {
        return Fencer.getFencingSchool();
    }
    
    public String getAge()
    {
        LocalDate birthday = LocalDate.parse(Fencer.getBirthday(), DateTimeFormatter.ISO_DATE);
        LocalDate now = LocalDate.now();
        Period age = Period.between(birthday, now);
        return Integer.toString(age.getYears());
    }
}
