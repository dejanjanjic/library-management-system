package net.etfbl.dobavljac.service;

import com.google.gson.Gson;
import net.etfbl.dobavljac.config.ConfigLoader;
import net.etfbl.dobavljac.logger.DobavljacLogger;
import net.etfbl.dobavljac.model.Bill;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class BookkeepingRMIService implements IBookkeepingRMIService{

    @Override
    public double saveBill(Bill bill) throws RemoteException {
        serializeBill(bill);
        double taxPercentage = Double.parseDouble(ConfigLoader.getInstance().getProperty("tax.percentage"));
        return taxPercentage*bill.getPrice();
    }

    private void serializeBill(Bill bill) {
        File file = new File(ConfigLoader.getInstance().getProperty("bills.path") + bill.getProccessingDate().getTime() + ".json");
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)), true)) {
            Gson gson = new Gson();
            pw.println(gson.toJson(bill));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try{
            BookkeepingRMIService server = new BookkeepingRMIService();

            IBookkeepingRMIService stub = (IBookkeepingRMIService) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(ConfigLoader.getInstance().getProperty("rmi.port")));
            registry.rebind(ConfigLoader.getInstance().getProperty("bookkeeping.rmi.class.name"), stub);
        }catch (RemoteException e){
            DobavljacLogger.logger.severe("Error: " + e);
        }
    }
}
