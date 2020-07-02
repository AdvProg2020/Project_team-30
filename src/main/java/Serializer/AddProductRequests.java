package Serializer;


import Model.Request.AddProductRequest;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AddProductRequests {
    private final static String pathName = "src\\main\\resources\\AddProductRequest.xml";

    @XmlElement(name = "admin")
    private ArrayList<AddProductRequest> allAddProductRequests = new ArrayList<>();
    private AddProductRequests(){
    }

    public static void deserializeXML(){
        File file = new File(pathName);
        if(file.exists() && !file.isDirectory() && file.length() > 0) {
            try(BufferedReader input = Files.newBufferedReader(Paths.get(pathName))) {
                AddProductRequests admins = JAXB.unmarshal(input, AddProductRequests.class);
                AddProductRequest.setAllAddProductRequest(admins.allAddProductRequests);
            } catch (IOException e) {
                System.out.println("Error opening file");
            }
        }
    }

    public static void serializeXML(){
        try(BufferedWriter output = Files.newBufferedWriter(Paths.get(pathName))) {
            AddProductRequests admins = new AddProductRequests();
            admins.allAddProductRequests = AddProductRequest.getAllAddProductRequest();
            JAXB.marshal(admins, output);
        }
        catch (IOException e) {
            System.out.println("Error opening file");
        }
    }
}
