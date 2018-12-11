package mutex.io;

import mutex.simulator.control.SimulationParameters;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompiledFile implements Serializable{

    // to avoid InvalidClassExceptions, while allowing me to add new parameters later,
    // the version ID stays constant
    //(note to self: fields can be added/removed, but types can't be changed)
    public static final long serialVersionUID = 1L;

    private SimulationParameters myParameters;

    public CompiledFile(File file) throws IOException
    {
        setSafeDefaults();
        readFile(file);
    }

    public CompiledFile()
    {
        setSafeDefaults();
    }

    public void setSafeDefaults()
    {
        myParameters = new SimulationParameters();
    }

    public SimulationParameters getParameters() {
        return myParameters;
    }

    public void setParameters(SimulationParameters parameters) {
        this.myParameters = parameters;
    }

    //TODO: make this into a proper deep copy. not sure if it's necessary at this point
    private void copyFile(CompiledFile file)
    {
        myParameters = file.getParameters();
    }

    /**
     * Code for reading property files borrowed from
     * http://www.mkyong.com/java/java-properties-file-examples/
     */
    private void readFile(File source) throws IOException
    {
        try(FileInputStream fileIn = new FileInputStream(source);
            GZIPInputStream zipIn = new GZIPInputStream(fileIn);
            ObjectInputStream in = new ObjectInputStream(zipIn))
        {
            CompiledFile file = (CompiledFile)in.readObject();
            copyFile(file);
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     * code for writing property files borrowed from
     * http://www.mkyong.com/java/java-properties-file-examples/
     */
    public void writeFile(File dest) throws IOException
    {
        try(FileOutputStream fileOut = new FileOutputStream(dest);
            GZIPOutputStream zipOut = new GZIPOutputStream(fileOut);
            ObjectOutputStream objOut = new ObjectOutputStream(zipOut))
        {
            objOut.writeObject(this);
        }
    }


}
