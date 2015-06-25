<?php

namespace WikibaseQuality\ConstraintReport\Maintenance;

// @codeCoverageIgnoreStart
use Wikibase\DataModel\Entity\ItemId;
use Wikibase\Repo\WikibaseRepo;
use WikibaseQuality\ConstraintReport\EvaluateConstraintReportJobService;


$basePath = getenv( "MW_INSTALL_PATH" ) !== false ? getenv( "MW_INSTALL_PATH" ) : __DIR__ . "/../../..";
require_once $basePath . "/maintenance/Maintenance.php";
// @codeCoverageIgnoreEnd

class ConstraintReport extends \Maintenance {

	public function __construct() {
		parent::__construct();
		$this->mDescription = "Checks constraints on items. Add --amount when you only want to check a certain amount of items (otherwise it will try to check 14000000 items which may take some years...). Add --start and/or --end when you want to specify a range. Otherwise, range will be set to 1 and 21000000.";
		$this->addOption( 'amount', 'number of items you want to check', false, true );
		$this->addOption( 'start', 'numeric item id the checks starts with', false, true );
		$this->addOption( 'end', 'numeric item id the checks ends with', false, true );
	}

	public function execute(){
		$amount = $this->getOption( 'amount' ) ? $this->getOption( 'amount' ) : 14000000;   
	    $start = $this->getOption( 'start' ) ? $this->getOption( 'start' ) : 1; 
	    $end = $this->getOption( 'end' ) ? $this->getOption( 'end' ) : 21000000; 
		$n = 0;
		$itemMap = array();

		while( $n <= $amount {
			$itemId = 'Q' . rand( start, end );
			if( array_key_exists( $itemId, $itemMap ) ) {
				continue;
			}
			$itemMap[$itemId] = true;
			$entity = $lookup->getEntity( new ItemId( $itemId ) );
			if ( $entity ) {
				$service = new EvaluateConstraintReportJobService();
				$params = array( 'entityId' => $itemId, 'referenceTimestamp' => null );
				$resultSummary = $service->getResults( $params );
				$messageToLog = $service->buildMessageForLog( $resultSummary, null, $params );
				$service->writeToLog( $messageToLog );	
				$amount += 1;
				echo "check $itemId; amount of items checked: $amount \n";
			}
		}
	}
}

// @codeCoverageIgnoreStart
$maintClass = 'WikibaseQuality\ConstraintReport\Maintenance\ConstraintReport';
require_once RUN_MAINTENANCE_IF_MAIN;