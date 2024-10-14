package hhplus.concertreservationservice.domain.user.service;

import hhplus.concertreservationservice.domain.queue.dto.QueueCommand;
import hhplus.concertreservationservice.domain.user.entity.User;
import hhplus.concertreservationservice.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public void findUserValidation(QueueCommand.Enqueue command){
        userRepository.findById(command.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }



}
