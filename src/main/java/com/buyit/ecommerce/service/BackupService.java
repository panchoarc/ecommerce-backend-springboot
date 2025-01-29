package com.buyit.ecommerce.service;

import java.io.IOException;

public interface BackupService {

    String createDatabaseBackup() throws IOException, InterruptedException;
}
