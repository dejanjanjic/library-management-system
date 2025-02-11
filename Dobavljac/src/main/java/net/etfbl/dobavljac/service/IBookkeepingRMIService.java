package net.etfbl.dobavljac.service;
import net.etfbl.dobavljac.model.Bill;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBookkeepingRMIService extends Remote{
    double saveBill(Bill bill) throws RemoteException;
}
