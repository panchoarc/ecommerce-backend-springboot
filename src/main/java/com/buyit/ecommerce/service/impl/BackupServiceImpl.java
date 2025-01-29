package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.exception.custom.BackupFailedException;
import com.buyit.ecommerce.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService {

    private final String backupPath;
    private static final String CONTAINER_NAME = "shared-postgres"; // Nombre del contenedor de PostgreSQL
    private static final String BACKUP_FOLDER_IN_CONTAINER = "/backup/"; // Ruta en el contenedor

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    public BackupServiceImpl() {
        this.backupPath = determineBackupPath();
        createDirectoryIfNotExists(backupPath);
    }

    @Override
    public String createDatabaseBackup() {
        String timestamp = generateTimestamp();
        String backupFilename = "backup_" + timestamp + ".sql";
        String containerBackupPath = BACKUP_FOLDER_IN_CONTAINER + backupFilename;
        String hostBackupPath = Paths.get(backupPath, backupFilename).toString();

        executeBackupInContainer(containerBackupPath);
        copyBackupToHost(containerBackupPath, hostBackupPath);
        return "Backup realizado con éxito en: " + hostBackupPath;

    }

    // 📌 Genera la fecha y hora actual en formato adecuado para el nombre del backup
    private String generateTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return LocalDateTime.now().format(formatter);
    }

    // 📌 Determina la ruta del backup en función del sistema operativo
    private String determineBackupPath() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ?
                Paths.get(System.getenv("USERPROFILE"), ".ecommerce", "backup").toString() :
                Paths.get(System.getProperty("user.home"), ".ecommerce", "backup").toString();
    }

    // 📌 Crea la carpeta si no existe
    private void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
            log.info("Carpeta de backups creada: {}", path);
        }
    }

    // 📌 Ejecuta el comando de backup dentro del contenedor Docker
    private void executeBackupInContainer(String containerBackupPath) {
        String dbName = extractDatabaseName();
        String command = String.format(
                "docker exec %s pg_dump -U %s -F p -b -f \"%s\" %s",
                CONTAINER_NAME, dbUser, containerBackupPath, dbName
        );

        log.info("Ejecutando backup en el contenedor: {}", command);
        executeCommand(command);
    }

    // 📌 Copia el backup desde el contenedor al host
    private void copyBackupToHost(String containerBackupPath, String hostBackupPath) {
        String command = String.format("docker cp %s:%s %s", CONTAINER_NAME, containerBackupPath, hostBackupPath);

        log.info("Copiando backup al host: {}", command);
        executeCommand(command);
    }

    // 📌 Extrae el nombre de la base de datos desde la URL de configuración
    private String extractDatabaseName() {
        return dbUrl.substring(dbUrl.lastIndexOf("/") + 1).split("\\?")[0];
    }

    // 📌 Método genérico para ejecutar comandos en la terminal y capturar errores
    private void executeCommand(String command) throws BackupFailedException {
        log.info("Ejecutando el comando: {}", command);

        String[] commandArray = buildCommandArray(command);

        try {
            Process process = startProcess(commandArray);
            handleProcessExit(process);
        } catch (IOException e) {
            log.error("Error al ejecutar el comando '{}'. Detalles: {}", command, e.getMessage());
            throw new BackupFailedException("Error de entrada/salida al ejecutar el comando: " + e.getMessage());
        }
    }

    private String[] buildCommandArray(String command) {
        return command.split(" ");
    }

    private Process startProcess(String[] commandArray) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(commandArray);
        pb.redirectErrorStream(true);
        return pb.start();
    }

    private void handleProcessExit(Process process) throws BackupFailedException {
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new BackupFailedException("El comando falló con código de salida: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserva el estado de la interrupción
            throw new BackupFailedException("El proceso fue interrumpido al esperar su finalización"+ e.getMessage());
        }
    }
}
