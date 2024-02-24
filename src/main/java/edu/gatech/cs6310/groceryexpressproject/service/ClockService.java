package edu.gatech.cs6310.groceryexpressproject.service;

import edu.gatech.cs6310.groceryexpressproject.model.Clock;
import edu.gatech.cs6310.groceryexpressproject.repository.ClockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ClockService {
    @Autowired
    private ClockRepository clockRepository;
    @Autowired
    private CustomerService customerService;

    public void initializeClock() {
        if(!isClockExists()) {
            Clock clock = new Clock();
            clockRepository.save(clock);
        };
    }

    private boolean isClockExists() {
        return clockRepository.findById(1L).isPresent();
    }

    public Clock getClock() {
        return clockRepository.findById(1L).get();
    }

    public int getCurrentTime() {
        Clock clock = clockRepository.findById(1L).get();
        return clock.getTime();
    }

    public void incrementTime(int time) {
        Clock clock = clockRepository.findById(1L).get();
        int newTime = clock.getTime() + time;
        clock.setTime(newTime);
        if (newTime - clock.getLastPurgeTime() >= 1440*7) {
            clock.setLastPurgeTime(newTime);
            try {
                customerService.exportCustomersToCSV(time);
                customerService.purgeCustomers();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        clockRepository.save(clock);
    }

    public void setCurrentTime(int time) {
        Clock clock = clockRepository.findById(1L).get();
        clock.setTime(time);
        if (time - clock.getLastPurgeTime() >= 1440*7) {
            clock.setLastPurgeTime(time);
            try {
                customerService.exportCustomersToCSV(time);
                customerService.purgeCustomers();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        clockRepository.save(clock);
    }

    public boolean isDayTime() {
        Clock clock = clockRepository.findById(1L).get();
        return clock.isDaytime(clock.getTime());
    }
}
