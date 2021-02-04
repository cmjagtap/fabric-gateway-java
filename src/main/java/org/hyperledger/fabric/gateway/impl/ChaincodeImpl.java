package org.hyperledger.fabric.gateway.impl;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.hyperledger.fabric.gateway.Chaincode;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.ChaincodeCollectionConfiguration;
import org.hyperledger.fabric.sdk.ChaincodeResponse;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.LifecycleApproveChaincodeDefinitionForMyOrgProposalResponse;
import org.hyperledger.fabric.sdk.LifecycleApproveChaincodeDefinitionForMyOrgRequest;
import org.hyperledger.fabric.sdk.LifecycleChaincodeEndorsementPolicy;
import org.hyperledger.fabric.sdk.LifecycleChaincodePackage;
import org.hyperledger.fabric.sdk.LifecycleCommitChaincodeDefinitionProposalResponse;
import org.hyperledger.fabric.sdk.LifecycleCommitChaincodeDefinitionRequest;
import org.hyperledger.fabric.sdk.LifecycleInstallChaincodeProposalResponse;
import org.hyperledger.fabric.sdk.LifecycleInstallChaincodeRequest;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import com.google.protobuf.InvalidProtocolBufferException;

public final class ChaincodeImpl implements Chaincode{

	@Override
	public LifecycleChaincodePackage createLifecycleChaincodePackage(String chaincodeLabel, Type chaincodeType,
			String chaincodeSourceLocation, String chaincodePath, String metadadataSource) throws IOException, InvalidArgumentException {
		// TODO Auto-generated method stub
		
        out("creating install package %s.", chaincodeLabel);

        Path metadataSourcePath = null;
        if (metadadataSource != null) {
            metadataSourcePath = Paths.get(metadadataSource);
        }
        LifecycleChaincodePackage lifecycleChaincodePackage = LifecycleChaincodePackage.fromSource(chaincodeLabel, Paths.get(chaincodeSourceLocation),
                chaincodeType,
                chaincodePath, metadataSourcePath);

        return lifecycleChaincodePackage;
	}

	@Override
	public String lifecycleInstallChaincode(HFClient client, Collection<Peer> peers,
			LifecycleChaincodePackage lifecycleChaincodePackage,int deployWaitTime)  throws InvalidArgumentException, ProposalException, InvalidProtocolBufferException{
		// TODO Auto-generated method stub
		
		    int numInstallProposal = 0;

	        numInstallProposal = numInstallProposal + peers.size();

	        LifecycleInstallChaincodeRequest installProposalRequest = client.newLifecycleInstallChaincodeRequest();
	        installProposalRequest.setLifecycleChaincodePackage(lifecycleChaincodePackage);
	       
	        installProposalRequest.setProposalWaitTime(deployWaitTime);

	        Collection<LifecycleInstallChaincodeProposalResponse> responses = client.sendLifecycleInstallChaincodeRequest(installProposalRequest, peers);

	        Collection<ProposalResponse> successful = new LinkedList<>();
	        Collection<ProposalResponse> failed = new LinkedList<>();
	        String packageID = null;
	        for (LifecycleInstallChaincodeProposalResponse response : responses) {
	            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
	                out("Successful install proposal response Txid: %s from peer %s", response.getTransactionID(), response.getPeer().getName());
	                successful.add(response);
	                if (packageID == null) {
	                    packageID = response.getPackageId();
	                } else {
	                }
	            } else {
	                failed.add(response);
	            }
	        }

	        out("Received %d install proposal responses. Successful+verified: %d . Failed: %d", numInstallProposal, successful.size(), failed.size());

	        if (failed.size() > 0) {
	            ProposalResponse first = failed.iterator().next();
	            out("install :" + successful.size() + ".  " + first.getMessage());

	        }

	        return packageID;

	}

	@Override
	public CompletableFuture<TransactionEvent> lifecycleApproveChaincodeDefinitionForMyOrg(HFClient client,
			Channel channel, Collection<Peer> peers, long sequence, String chaincodeName, String chaincodeVersion,
			LifecycleChaincodeEndorsementPolicy chaincodeEndorsementPolicy,
			ChaincodeCollectionConfiguration chaincodeCollectionConfiguration, boolean initRequired,
			String org1ChaincodePackageID) throws InvalidArgumentException, ProposalException  {
		// TODO Auto-generated method stub

        LifecycleApproveChaincodeDefinitionForMyOrgRequest lifecycleApproveChaincodeDefinitionForMyOrgRequest = client.newLifecycleApproveChaincodeDefinitionForMyOrgRequest();
        lifecycleApproveChaincodeDefinitionForMyOrgRequest.setSequence(sequence);
        lifecycleApproveChaincodeDefinitionForMyOrgRequest.setChaincodeName(chaincodeName);
        lifecycleApproveChaincodeDefinitionForMyOrgRequest.setChaincodeVersion(chaincodeVersion);
        lifecycleApproveChaincodeDefinitionForMyOrgRequest.setInitRequired(initRequired);

        if (null != chaincodeCollectionConfiguration) {
            lifecycleApproveChaincodeDefinitionForMyOrgRequest.setChaincodeCollectionConfiguration(chaincodeCollectionConfiguration);
        }

        if (null != chaincodeEndorsementPolicy) {
            lifecycleApproveChaincodeDefinitionForMyOrgRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        }

        lifecycleApproveChaincodeDefinitionForMyOrgRequest.setPackageId(org1ChaincodePackageID);

        Collection<LifecycleApproveChaincodeDefinitionForMyOrgProposalResponse> lifecycleApproveChaincodeDefinitionForMyOrgProposalResponse = channel.sendLifecycleApproveChaincodeDefinitionForMyOrgProposal(lifecycleApproveChaincodeDefinitionForMyOrgRequest,
                peers);

        for (LifecycleApproveChaincodeDefinitionForMyOrgProposalResponse response : lifecycleApproveChaincodeDefinitionForMyOrgProposalResponse) {
            final Peer peer = response.getPeer();

        }

        return channel.sendTransaction(lifecycleApproveChaincodeDefinitionForMyOrgProposalResponse);

}

	@Override
	public CompletableFuture<TransactionEvent> commitChaincodeDefinitionRequest(HFClient client, Channel channel,
			long definitionSequence, String chaincodeName, String chaincodeVersion,
			LifecycleChaincodeEndorsementPolicy chaincodeEndorsementPolicy,
			ChaincodeCollectionConfiguration chaincodeCollectionConfiguration, boolean initRequired,
			Collection<Peer> endorsingPeers) throws ProposalException, InvalidArgumentException, InterruptedException, ExecutionException, TimeoutException {
		// TODO Auto-generated method stub
        LifecycleCommitChaincodeDefinitionRequest lifecycleCommitChaincodeDefinitionRequest = client.newLifecycleCommitChaincodeDefinitionRequest();

        lifecycleCommitChaincodeDefinitionRequest.setSequence(definitionSequence);
        lifecycleCommitChaincodeDefinitionRequest.setChaincodeName(chaincodeName);
        lifecycleCommitChaincodeDefinitionRequest.setChaincodeVersion(chaincodeVersion);
        if (null != chaincodeEndorsementPolicy) {
            lifecycleCommitChaincodeDefinitionRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        }
        if (null != chaincodeCollectionConfiguration) {
            lifecycleCommitChaincodeDefinitionRequest.setChaincodeCollectionConfiguration(chaincodeCollectionConfiguration);
        }
        lifecycleCommitChaincodeDefinitionRequest.setInitRequired(initRequired);

        Collection<LifecycleCommitChaincodeDefinitionProposalResponse> lifecycleCommitChaincodeDefinitionProposalResponses = channel.sendLifecycleCommitChaincodeDefinitionProposal(lifecycleCommitChaincodeDefinitionRequest,
                endorsingPeers);

        for (LifecycleCommitChaincodeDefinitionProposalResponse resp : lifecycleCommitChaincodeDefinitionProposalResponses) {

            final Peer peer = resp.getPeer();
        }

        return channel.sendTransaction(lifecycleCommitChaincodeDefinitionProposalResponses);
	}
    static void out(String format, Object... args) {

        System.err.flush();
        System.out.flush();

        System.out.println(format(format, args));
        System.err.flush();
        System.out.flush();

    }
}
