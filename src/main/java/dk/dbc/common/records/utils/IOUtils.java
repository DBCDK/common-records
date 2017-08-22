/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records.utils;


import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.io.*;

/**
 * @brief Implements utility functions for IO.
 */
public class IOUtils {
    private static final XLogger logger = XLoggerFactory.getXLogger( IOUtils.class );

    /**
     * @brief Returns an input stream for a given resource.
     * 
     * The resource is located in the class path and not only by the 
     * current jar.
     * 
     * @param name The name of the resource.
     * 
     * @return An InputStream if the resource exists. null otherwise.
     */
    public static InputStream getResourceAsStream( String name ) {
        return IOUtils.class.getClassLoader().getResourceAsStream( name );
    }
    
    /**
     * @brief Reads all content from a resource file and returns it.
     * 
     * The resource is assumed to be a text resource.
     * 
     * @param resName The resource name.
     * 
     * @return The content of the resource.
     * 
     * @throws IOException In case of IO failures.
     */
    public static String readAll( String resName ) throws IOException {
        return readAll( resName, "UTF-8" );
    }

    /**
     * @brief Reads all content from a resource file and returns it.
     * 
     * The resource is assumed to be a text resource.
     * 
     * @param resName  The resource name.
     * @param encoding Name for the encoding (charset) to use.
     * 
     * @return The content of the resource.
     * 
     * @throws IOException In case of IO failures.
     */
    public static String readAll( String resName, String encoding ) throws IOException {
        return readAll( getResourceAsStream( resName ), encoding );
    }
   
    /**
     * @brief Reads all content from an InputStream and returns it.
     * 
     * The InputStream is assumed to be a text resource.
     * 
     * @param in       InputStream
     * @param encoding Name for the encoding (charset) to use.
     * 
     * @return The content of the resource.
     * 
     * @throws IOException In case of IO failures.
     * @throws UnsupportedEncodingException If the encoding is unknown.
     */
    public static String readAll( InputStream in, String encoding ) throws UnsupportedEncodingException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        
        return new String( baos.toByteArray(), encoding );
    }

    //-----------------------------------------------------------------------------
    //              Files & Directories
    //-----------------------------------------------------------------------------

    public static boolean exists( File baseDir, String filename ) throws IOException {
        logger.entry( baseDir, filename );

        try {
            return exists( baseDir.getCanonicalPath() + "/" + filename );
        }
        finally {
            logger.exit();
        }
    }

    public static boolean exists( String filename ) throws IOException {
        logger.entry( filename );

        try {
            return new File( filename ).exists();
        }
        finally {
            logger.exit();
        }
    }

}
