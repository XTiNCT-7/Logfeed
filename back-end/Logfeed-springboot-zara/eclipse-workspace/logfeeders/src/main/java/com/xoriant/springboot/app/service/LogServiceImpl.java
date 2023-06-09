package com.xoriant.springboot.app.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xoriant.springboot.app.exception.DataNotFoundException;
import com.xoriant.springboot.app.models.Log;
import com.xoriant.springboot.app.repository.LogRepository;

@Service
public class LogServiceImpl implements LogService {

	@Autowired
	LogRepository logRepository;
	private Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

	@Autowired
	private LogFetcher logFetcher;

	@Override
	public List<String> displayLogs() {
		System.out.println(Timestamp.from(Instant.now()));

		return null;
	}

	@Override
	public void readLogs() {
		try {
			String folder = "Documents";
			String filename = "newlog.log";
			BufferedReader reader = new BufferedReader(
					new FileReader("C:\\Users\\qureshi_z\\" + folder + "\\" + filename));
			String line;
			while ((line = reader.readLine()) != null) {
				parseLogEntry(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void parseLogEntry(String logEntry) {
		String[] logComponents = logEntry.split(" ");

		String date = logComponents[0] + " " + logComponents[1];
		String time = logComponents[2];
		String systemName = logComponents[3];
		String logType = logComponents[4];
		String message = String.join(" ", Arrays.copyOfRange(logComponents, 5, logComponents.length));
		System.out.println("logType" + logType);
		// Check if logType requires special handling
		if (logType.contains(":")) {
			String[] typeComponents = logType.split(":");
			if (typeComponents.length > 1) {

				String suffix = typeComponents[1].trim();
				if (suffix.matches(".*[A-Z].*")) {
					logType = suffix;
				}
			} else {
				logType = "INFORMATION";
			}
		}

		System.out.println("Date: " + date);
		System.out.println("Time: " + time);
		System.out.println("System Name: " + systemName);
		System.out.println("Log Type: " + logType);
		System.out.println("Message: " + message);
		System.out.println("-----");
	}

	@Override
	public List<Log> getLogs() throws DataNotFoundException {
		logFetcher.processLog();
		return logRepository.findAll();
	}

	@Override
	public List<Log> getLogsByDate(String date) throws DataNotFoundException {
		List<Timestamp> ts = logRepository.getTimeStampLogs();
		List<Log> sortedLogs = new LinkedList<Log>();

		ts.forEach((v) -> {
			LocalDate justDate = v.toLocalDateTime().toLocalDate();

			String d = justDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

			if (date.equals(d)) {

				sortedLogs.addAll(logRepository.getBytimestamp(v));
			}

		});

		return sortedLogs;
	}

	@Override
	public List<Log> getDataByTime(int time) throws DataNotFoundException {

		return logRepository.getDataByTime(time);
	}

	@Override
	public List<Log> getDataBySystem(String system) throws DataNotFoundException {

		return logRepository.getBySystemName(system);
	}

	@Override
	public List<Log> searchLogs(String searchTerm) throws DataNotFoundException {
		// TODO Auto-generated method stub
		return logRepository.searchLogs(searchTerm);
	}

	@Override
	public int getLengthofLogs() {

		return logRepository.findAll().size();
	}

}
