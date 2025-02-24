package com.buyit.ecommerce.service;

import java.io.IOException;

public interface DatabaseService {

    String createDatabaseBackup() throws IOException, InterruptedException;

    String restoreDatabaseBackup();
}
