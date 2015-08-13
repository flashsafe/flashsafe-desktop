package ru.flashsafe.core.old.storage.rest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.message.internal.AbstractMessageReaderWriterProvider;
import org.glassfish.jersey.message.internal.ReaderWriter;
import org.glassfish.jersey.message.internal.Utils;

import ru.flashsafe.core.operation.OperationState;
import ru.flashsafe.core.storage.StorageOperationStatusImpl;

@Produces({"application/octet-stream", "*/*"})
@Consumes({"application/octet-stream", "*/*"})
@Deprecated
public class FileTransferProgressMonitorableProvider extends AbstractMessageReaderWriterProvider<FileWithStatus> {
    
    @Override
    public boolean isReadable(final Class<?> type,
                              final Type genericType,
                              final Annotation[] annotations,
                              final MediaType mediaType) {
        return FileWithStatus.class == type;
    }

    @Override
    public FileWithStatus readFrom(final Class<FileWithStatus> type,
                         final Type genericType,
                         final Annotation[] annotations,
                         final MediaType mediaType,
                         final MultivaluedMap<String, String> httpHeaders,
                         final InputStream entityStream) throws IOException {
        final File file = Utils.createTempFile();
        final OutputStream stream = new BufferedOutputStream(new FileOutputStream(file));

        try {
            writeTo(entityStream, stream);
        } finally {
            stream.close();
        }
        //TODO fix
        return new FileWithStatus(file, null);
    }

    @Override
    public boolean isWriteable(final Class<?> type,
                               final Type genericType,
                               final Annotation[] annotations,
                               final MediaType mediaType) {
        return FileWithStatus.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(final FileWithStatus t,
                        final Class<?> type,
                        final Type genericType,
                        final Annotation[] annotations,
                        final MediaType mediaType,
                        final MultivaluedMap<String, Object> httpHeaders,
                        final OutputStream entityStream) throws IOException {
        final InputStream stream = new BufferedInputStream(new FileInputStream(t.getFile()), ReaderWriter.BUFFER_SIZE);

        try {
            writeTo(stream, new CountOutputStream(entityStream, t.getStatus()));
        } finally {
            stream.close();
        }
    }

    @Override
    public long getSize(final FileWithStatus t,
                        final Class<?> type,
                        final Type genericType,
                        final Annotation[] annotations,
                        final MediaType mediaType) {
        return t.getFile().length();
    }
    
    private static final class CountOutputStream extends OutputStream {

        private final OutputStream outputStream;
        
        private final StorageOperationStatusImpl status;
        
        public CountOutputStream(OutputStream outputStream, StorageOperationStatusImpl status) {
            this.outputStream = outputStream;
            this.status = status;
            status.setState(OperationState.IN_PROGRESS);
        }
        
        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
            status.incrementProcessedBytes(1);
        }
        
        @Override
        public void write(byte[] b) throws IOException {
            outputStream.write(b);
            status.incrementProcessedBytes(b.length);
        }
        
        
        
    }
}
