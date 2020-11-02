package com.phytel.patient.match.model;

import java.io.Serializable;

/*
 * Enum which holds all the datastores
 * */
public enum DataStores implements Serializable {
	CONTACTENTITIES,PATIENTMPI;

    public String toString(){
        switch(this){
        case CONTACTENTITIES :
            return "contactEntities";
        case PATIENTMPI :
            return "patientMPI";
        }
        return null;
    }
}
