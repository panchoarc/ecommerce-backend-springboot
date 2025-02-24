package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.exception.custom.BackupFailedException;
import com.buyit.ecommerce.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {

    private final String backupPath;
    private static final String CONTAINER_NAME = "shared-postgres"; // Nombre del contenedor de PostgreSQL
    private static final String BACKUP_FOLDER_IN_CONTAINER = "/backup/"; // Ruta en el contenedor
    private static final Integer RETENTION_DAYS = 7;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    public DatabaseServiceImpl() {
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
        return "Backup realizado con éxito en: " + hostBackupPath;

    }

    @Override
    public String restoreDatabaseBackup() {
        return "";
    }


    @Scheduled(cron = "${spring.scheduling.cron.backup-cleanup}") // Se ejecuta a medianoche todos los días
    public void deleteOldBackups() {
        log.info("Ejecutando tarea programada de eliminación de backups...");

        try {
            File backupDir = new File(backupPath);
            if (!backupDir.exists() || !backupDir.isDirectory()) {
                log.warn("La carpeta de backups no existe o no es un directorio: {}", backupPath);
                return;
            }

            File[] files = backupDir.listFiles();
            if (files == null || files.length == 0) {
                log.info("No hay backups en la carpeta.");
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            for (File file : files) {
                try {
                    if (file.isFile() && file.getName().endsWith(".sql")) {
                        LocalDateTime fileTime = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(file.lastModified()), java.time.ZoneId.systemDefault());

                        long daysBetween = ChronoUnit.DAYS.between(fileTime, now);
                        log.info("Revisando archivo: {} ({} días de antigüedad)", file.getName(), daysBetween);

                        if (daysBetween >= RETENTION_DAYS) {
                            if (file.delete()) {
                                log.info("Backup eliminado: {}", file.getName());
                            } else {
                                log.error("No se pudo eliminar el backup: {}", file.getName());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Error al procesar el archivo: {}", file.getName(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error general al ejecutar la eliminación de backups", e);
        }
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
                "docker exec %s pg_dump -U %s --column-inserts -f \"%s\" %s",
                CONTAINER_NAME, dbUser, containerBackupPath, dbName
        );

        log.info("Ejecutando backup en el contenedor: {}", command);
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
