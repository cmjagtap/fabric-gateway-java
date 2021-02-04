/*
 * Email: cmjagtap1@gmail.com.
 *
 */

package org.hyperledger.fabric.gateway;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.hyperledger.fabric.sdk.ChaincodeCollectionConfiguration;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.LifecycleChaincodeEndorsementPolicy;
import org.hyperledger.fabric.sdk.LifecycleChaincodePackage;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import com.google.protobuf.InvalidProtocolBufferException;

public interface Chaincode {

	LifecycleChaincodePackage createLifecycleChaincodePackage(String chaincodeLabel, Type chaincodeType,
			String chaincodeSourceLocation, String chaincodePath, String metadadataSource)
			throws IOException, InvalidArgumentException;

	String lifecycleInstallChaincode(HFClient client, Collection<Peer> peers,
			LifecycleChaincodePackage lifecycleChaincodePackage, int deployWaitTime)
			throws InvalidArgumentException, ProposalException, InvalidProtocolBufferException;

	CompletableFuture<TransactionEvent> lifecycleApproveChaincodeDefinitionForMyOrg(HFClient client, Channel channel,
			Collection<Peer> peers, long sequence, String chaincodeName, String chaincodeVersion,
			LifecycleChaincodeEndorsementPolicy chaincodeEndorsementPolicy,
			ChaincodeCollectionConfiguration chaincodeCollectionConfiguration, boolean initRequired,
			String org1ChaincodePackageID) throws InvalidArgumentException, ProposalException;

	CompletableFuture<TransactionEvent> commitChaincodeDefinitionRequest(HFClient client, Channel channel,
			long definitionSequence, String chaincodeName, String chaincodeVersion,
			LifecycleChaincodeEndorsementPolicy chaincodeEndorsementPolicy,
			ChaincodeCollectionConfiguration chaincodeCollectionConfiguration, boolean initRequired,
			Collection<Peer> endorsingPeers) throws ProposalException, InvalidArgumentException, InterruptedException,
			ExecutionException, TimeoutException;

}
