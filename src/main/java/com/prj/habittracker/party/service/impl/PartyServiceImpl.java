package com.prj.habittracker.party.service.impl;

import com.prj.habittracker.common.enums.PartyRole;
import com.prj.habittracker.common.enums.PartyStatus;
import com.prj.habittracker.common.security.CurrentUserService;
import com.prj.habittracker.party.dto.request.*;
import com.prj.habittracker.party.dto.response.PartyMemberResponse;
import com.prj.habittracker.party.dto.response.PartyResponse;
import com.prj.habittracker.party.entity.PartyEntity;
import com.prj.habittracker.party.entity.PartyMemberEntity;
import com.prj.habittracker.party.repository.PartyMemberRepository;
import com.prj.habittracker.party.repository.PartyRepository;
import com.prj.habittracker.party.service.PartyService;
import com.prj.habittracker.user.entity.UserEntity;
import com.prj.habittracker.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartyServiceImpl implements PartyService {
    private static final int MAX_MEMBERS = 5;
    private static final int INVITE_CODE_LENGTH = 8;
    private static final char[] INVITE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PartyRepository partyRepository;
    private final PartyMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public Optional<PartyResponse> getCurrentParty() {
        UUID userId = currentUserService.getCurrentUserId();
        return memberRepository.findByUserId(userId).map(member -> toResponse(member.getParty(), userId));
    }

    @Override
    @Transactional
    public PartyResponse create(CreatePartyRequest request) {
        UUID userId = currentUserService.getCurrentUserId();
        UserEntity user = lockUser(userId);
        ensureNotInParty(userId);

        PartyEntity party = new PartyEntity();
        party.setName(request.name().trim());
        party.setLeader(user);
        party.setInviteCode(generateInviteCode());
        party.setStatus(PartyStatus.ACTIVE);
        party.setMaxMembers(MAX_MEMBERS);
        partyRepository.saveAndFlush(party);

        PartyMemberEntity member = new PartyMemberEntity();
        member.setParty(party);
        member.setUser(user);
        member.setRole(PartyRole.LEADER);
        memberRepository.saveAndFlush(member);
        return toResponse(party, userId);
    }

    @Override
    @Transactional
    public PartyResponse join(JoinPartyRequest request) {
        UUID userId = currentUserService.getCurrentUserId();
        UserEntity user = lockUser(userId);
        ensureNotInParty(userId);
        String inviteCode = request.inviteCode().trim().toUpperCase(Locale.ROOT);
        PartyEntity party = partyRepository.findByInviteCodeForUpdate(inviteCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Party invite code not found"));
        ensureActive(party);
        if (memberRepository.countByPartyId(party.getId()) >= party.getMaxMembers()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Party is full");
        }

        PartyMemberEntity member = new PartyMemberEntity();
        member.setParty(party);
        member.setUser(user);
        member.setRole(PartyRole.MEMBER);
        memberRepository.saveAndFlush(member);
        return toResponse(party, userId);
    }

    @Override
    @Transactional
    public PartyResponse update(UpdatePartyRequest request) {
        LockedParty context = lockCurrentParty();
        requireLeader(context.membership());
        context.party().setName(request.name().trim());
        return toResponse(context.party(), context.userId());
    }

    @Override
    @Transactional
    public PartyResponse refreshInviteCode() {
        LockedParty context = lockCurrentParty();
        requireLeader(context.membership());
        context.party().setInviteCode(generateInviteCode());
        partyRepository.flush();
        return toResponse(context.party(), context.userId());
    }

    @Override
    @Transactional
    public PartyResponse kick(UUID memberUserId) {
        LockedParty context = lockCurrentParty();
        requireLeader(context.membership());
        if (context.userId().equals(memberUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Leader cannot kick themselves");
        }
        PartyMemberEntity target = memberRepository
                .findByPartyIdAndUserIdForUpdate(context.party().getId(), memberUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Party member not found"));
        memberRepository.delete(target);
        memberRepository.flush();
        return toResponse(context.party(), context.userId());
    }

    @Override
    @Transactional
    public PartyResponse transferLeadership(TransferLeadershipRequest request) {
        LockedParty context = lockCurrentParty();
        requireLeader(context.membership());
        if (context.userId().equals(request.userId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already the party leader");
        }
        PartyMemberEntity target = memberRepository
                .findByPartyIdAndUserIdForUpdate(context.party().getId(), request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Party member not found"));
        context.membership().setRole(PartyRole.MEMBER);
        target.setRole(PartyRole.LEADER);
        context.party().setLeader(target.getUser());
        memberRepository.flush();
        return toResponse(context.party(), context.userId());
    }

    @Override
    @Transactional
    public void leave() {
        LockedParty context = lockCurrentParty();
        if (context.membership().getRole() == PartyRole.LEADER) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Transfer leadership or disband the party before leaving");
        }
        memberRepository.delete(context.membership());
    }

    @Override
    @Transactional
    public void disband() {
        LockedParty context = lockCurrentParty();
        requireLeader(context.membership());
        context.party().setStatus(PartyStatus.DISBANDED);
        memberRepository.deleteAllByPartyId(context.party().getId());
    }

    private LockedParty lockCurrentParty() {
        UUID userId = currentUserService.getCurrentUserId();
        lockUser(userId);
        PartyMemberEntity membershipSnapshot = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "You are not in a party"));
        PartyEntity party = partyRepository.findByIdForUpdate(membershipSnapshot.getParty().getId())
                .orElseThrow(() -> new EntityNotFoundException("Party not found"));
        PartyMemberEntity membership = memberRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "You are not in a party"));
        ensureActive(party);
        return new LockedParty(userId, membership, party);
    }

    private UserEntity lockUser(UUID userId) {
        return userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private void ensureNotInParty(UUID userId) {
        if (memberRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already in a party");
        }
    }

    private void ensureActive(PartyEntity party) {
        if (party.getStatus() != PartyStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Party is no longer active");
        }
    }

    private void requireLeader(PartyMemberEntity membership) {
        if (membership.getRole() != PartyRole.LEADER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the party leader can do this");
        }
    }

    private String generateInviteCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            StringBuilder code = new StringBuilder(INVITE_CODE_LENGTH);
            for (int index = 0; index < INVITE_CODE_LENGTH; index++) {
                code.append(INVITE_ALPHABET[RANDOM.nextInt(INVITE_ALPHABET.length)]);
            }
            if (!partyRepository.existsByInviteCode(code.toString())) return code.toString();
        }
        throw new IllegalStateException("Could not generate a unique party invite code");
    }

    private PartyResponse toResponse(PartyEntity party, UUID currentUserId) {
        List<PartyMemberResponse> members = memberRepository.findAllByPartyIdOrderByJoinedAtAsc(party.getId()).stream()
                .map(member -> new PartyMemberResponse(member.getUser().getId(), member.getUser().getUsername(),
                        member.getUser().getAvatarUrl(), member.getRole(), member.getJoinedAt()))
                .toList();
        return new PartyResponse(party.getId(), party.getName(), party.getInviteCode(), party.getMaxMembers(),
                members.size(), party.getLeader().getId(), party.getLeader().getId().equals(currentUserId),
                party.getCreatedAt(), members);
    }

    private record LockedParty(UUID userId, PartyMemberEntity membership, PartyEntity party) {}
}
