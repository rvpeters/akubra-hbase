package eu.scapeproject.akubra;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import org.akubraproject.Blob;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.DuplicateBlobException;
import org.akubraproject.MissingBlobException;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HBaseBlob implements Blob {
    private static final Logger log=LoggerFactory.getLogger(HBaseBlob.class);
    
    private final HBaseBlobStoreConnection conn;
    private final URI id;
    private final Get get;
    private final URI storeId; 

    public HBaseBlob(final URI id, final HBaseBlobStoreConnection conn) {
        super();
        this.conn = conn;
        this.storeId = this.conn.getBlobStore().getId();
        this.id=id;
        this.get=new Get(id.toASCIIString().getBytes());
        System.out.println("created Blob " + id.toASCIIString());
    }

    public void delete() throws IOException {
        System.out.println("deleting blob " + id.toASCIIString());
        Delete del=new Delete(id.toASCIIString().getBytes());
        this.conn.getTable().delete(del);
    }

    public boolean exists() throws IOException {
        System.out.println("checking existance of blob " + id.toASCIIString());
        return this.conn.getTable().exists(get);
    }

    public URI getCanonicalId() throws IOException {
        return id;
    }

    public BlobStoreConnection getConnection() {
        return conn;
    }

    public URI getId() {
        return id;
    }

    public long getSize() throws IOException, MissingBlobException {
        byte[] val= this.conn.getTable().get(get).getValue(HBaseBlobStore.DATA_FAMILY, HBaseBlobStore.DEFAULT_QUALIFIER);
        int len = 0;
        if (val != null) {
          len = val.length;
        }
        return len;
    }

    public Blob moveTo(URI arg0, Map<String, String> arg1) throws DuplicateBlobException, IOException, MissingBlobException, NullPointerException,
            IllegalArgumentException {
        System.out.println("moving blob " + id.toASCIIString());
        HBaseBlob moved=new HBaseBlob(arg0, this.conn);
        OutputStream os=null;
        InputStream is=null;
        try{
            os=moved.openOutputStream(-1,false);
            is=openInputStream();
            IOUtils.copy(is,os);
            this.delete();
            return moved;
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    public InputStream openInputStream() throws IOException, MissingBlobException {
        System.out.println("readin blob " + id.toASCIIString());
        byte[] data=this.conn.getTable().get(get).getValue(HBaseBlobStore.DATA_FAMILY, HBaseBlobStore.DEFAULT_QUALIFIER);
        if (data == null) {
            data = new byte[0];
        }
        return new ByteArrayInputStream(data);
    }

    public OutputStream openOutputStream(long arg0, boolean arg1) throws IOException, DuplicateBlobException {
        System.out.println("writing blob " + id.toASCIIString());
        return new HBaseOutputStream(this.conn.getTable(),id.toASCIIString().getBytes());
    }

}
